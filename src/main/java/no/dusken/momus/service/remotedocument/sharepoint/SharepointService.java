package no.dusken.momus.service.remotedocument.sharepoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.microsoft.aad.adal4j.AuthenticationResult;

import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.model.Article;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.ArticleService;
import no.dusken.momus.service.KeyValueService;
import no.dusken.momus.service.PersonService;
import no.dusken.momus.service.remotedocument.RemoteDocumentService;
import no.dusken.momus.service.remotedocument.sharepoint.models.DeltaItem;
import no.dusken.momus.service.remotedocument.sharepoint.models.DriveItem;
import no.dusken.momus.service.remotedocument.sharepoint.models.DriveItemChildren;
import no.dusken.momus.service.remotedocument.sharepoint.models.ItemDeltaList;
import no.dusken.momus.service.remotedocument.sharepoint.models.User;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.PersonRepository;

@Slf4j
@Service("remoteDocumentService")
public class SharepointService implements RemoteDocumentService {

    @Value("${sharepoint.syncEnabled}")
    private boolean enabled;

    @Value("${sharepoint.resource}")
    private String RESOURCE;

    @Value("${sharepoint.siteid}")
    private String SITE_ID;

    @Value("${sharepoint.driveid}")
    private String ARTICLES_DRIVE_ID;

    private Map<String, String> defaultParameters;

    private boolean isInitialized = false;

    private final String ROOT_URL = "{resource}/v1.0";
    private final String DRIVE_URL = ROOT_URL + "/drives/{driveId}";
    private final String ARTICLE_ITEM_URL = DRIVE_URL + "/items/{itemId}";
    private final String CREATE_ARTICLE_URL = DRIVE_URL + "/items/root:/{fileName}:/content";
    private final String ARTICLE_CONTENT_URL = DRIVE_URL + "/items/{itemId}/content";
    private final String DELTA_URL_NO_TOKEN = DRIVE_URL + "/root/delta";

    private final String USER_URL = ROOT_URL + "/users/{userId}";

    private final String DELTA_URL_KEY = "SHAREPOINT_DELTA_URL";

    private RestTemplate restTemplate;

    private SharepointAuthenticator sharepointAuthenticator;
    private SharepointTextConverter textConverter;

    private KeyValueService keyValueService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PersonRepository personRepository;

    public SharepointService(
        SharepointAuthenticator sharepointAuthenticator,
        KeyValueService keyValueService) {
        this.sharepointAuthenticator = sharepointAuthenticator;
        this.keyValueService = keyValueService;
    }

    @PostConstruct
    public void setup() {
        if (!enabled) {
            log.info("Not setting up Sharepoint");
            return;
        }

        if(isInitialized) {
            log.info("Sharepoint already initialized!");
            return;
        }

        log.info("Setting up Sharepoint");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("siteId", SITE_ID);
        parameters.put("driveId", ARTICLES_DRIVE_ID);
        parameters.put("resource", RESOURCE);

        this.defaultParameters = parameters;

        AuthenticationResult authToken;
        try {
            authToken = sharepointAuthenticator.getAccessToken();
        } catch (Exception e) {
            log.error("Failed to authenticate with Sharepoint {}", e);
            return;
        }

        this.restTemplate = setupRestTemplate(authToken.getAccessToken());
        this.textConverter = new SharepointTextConverter();

        isInitialized = true;

        sync();
    }

    @Scheduled(cron = "30 * * * * *")
    public void sync() {
        if(!enabled) {
            return;
        }
        log.debug("Starting Sharepoint sync");

        try {
            Set<DeltaItem> deltas = getDeltas();
            if(deltas.size() == 0) {
                log.info("No changes from Sharepoint");
            } else {
                log.info("Got deltas: {}", deltas.size());
                applyDeltas(deltas);
            }
        } catch (URISyntaxException e) {
            log.error("Could not get deltas from Sharepoint", e);
        }

        log.debug("Sharepoint sync finished");
    }

    public ServiceName getServiceName() {
        return ServiceName.SHAREPOINT;
    }

    public DriveItem createDocument(String name) throws IOException {
        log.info("Creating sharepoint file with name {}", name);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("fileName", name + ".docx");


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        PathResource resource = new PathResource(new ClassPathResource("sharepoint-template.docx").getFile().getPath());
        HttpEntity<PathResource> entity = new HttpEntity<>(resource, headers);
        try {
            ResponseEntity<DriveItem> resp = this.restTemplate.exchange(CREATE_ARTICLE_URL, HttpMethod.PUT, entity, DriveItem.class, parameters);
            return resp.getBody();
        } catch(HttpClientErrorException e) {
            if(e.getStatusCode().equals(HttpStatus.CONFLICT)) {
                String newName = name + "_new";
                log.info("Naming conflict of new file {}, giving new name {}", name, newName);
                return createDocument(newName);
            } else {
                throw e;
            }
        }
    }

    private String getItemContentAsHtml(DriveItem item) throws IOException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("itemId", item.getId());
        Resource res = this.restTemplate.getForObject(ARTICLE_CONTENT_URL, Resource.class, parameters);
        XWPFDocument doc = new XWPFDocument(res.getInputStream());
        OutputStream out = new ByteArrayOutputStream();

        XHTMLConverter.getInstance().convert(doc, out, XHTMLOptions.create());
        String html = out.toString();

        return this.textConverter.convert(html);
    }

    private DriveItem getDriveItem(String id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("itemId", id);
        DriveItem res = this.restTemplate.getForObject(ARTICLE_ITEM_URL, DriveItem.class, parameters);
        return res;
    }

    private User getUser(String id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userId", id);
        User res = this.restTemplate.getForObject(USER_URL, User.class, parameters);
        return res;
    }

    private Set<DeltaItem> getDeltas() throws URISyntaxException {
        String deltaLink = keyValueService.getValue(DELTA_URL_KEY);
        URI url = this.restTemplate.getUriTemplateHandler().expand(DELTA_URL_NO_TOKEN, this.defaultParameters);
        if(deltaLink != null) {
            url = new URI(deltaLink);
        }
        
        List<DeltaItem> deltas = new ArrayList<>();
        String nextDeltaLink;

        while(true) {
            ItemDeltaList deltaList = this.restTemplate.getForObject(url, ItemDeltaList.class);
            deltas.addAll(deltaList.getValue());

            // We have reached the end of the current delta set, so store link to next delta and break
            if(deltaList.getNextLink() == null) {
                nextDeltaLink = deltaList.getDeltaLink();
                break;
            }
            url = new URI(deltaList.getNextLink());
        }

        if(deltas.size() == 0) {
            return Collections.emptySet();
        }

        // Store the link for next sync
        keyValueService.setValue(DELTA_URL_KEY, nextDeltaLink);

        // We only want the valid deltas for us, which means no folders, not deleted
        // And we also only need one delta per changed article for every sync, so we make it a set.
        Set<DeltaItem> validDeltas = deltas.stream()
                .filter(item -> item.getFolder() == null && item.getDeleted() == null)
                .collect(Collectors.toSet());

        return validDeltas;
    }

    private void applyDeltas(Set<DeltaItem> deltas) {
        deltas.forEach(delta -> {
            try {
                DriveItem item = getDriveItem(delta.getId());
                String content = getItemContentAsHtml(item);
                Article article = articleRepository.findByRemoteId(item.getId());
                if(article == null) {
                    log.debug("Item {} was not found in the database, skipping", item.getName());
                    return;
                }
                User u = getUser(item.getLastModifiedBy().getUser().getId());
                Person dbPerson = personRepository.findByUsername(u.getUsername());
                log.debug("Article {} was changed by: {}", item.getName(), dbPerson);
                articleService.updateArticleContent(article.getId(), content, dbPerson);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (HttpClientErrorException e) {
                log.error("Error applying delta {} {}", delta, e.getStatusCode());
            }
        });
    }

    private RestTemplate setupRestTemplate(String authToken) {
        RestTemplate rest = new RestTemplate();

        rest.setDefaultUriVariables(this.defaultParameters);
        rest.setInterceptors(Collections.singletonList(new ClientHttpRequestInterceptor(){
        
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                    throws IOException {
                request.getHeaders().set("Authorization", "Bearer " + authToken);
                return execution.execute(request, body);
            }
        }));

        return rest;
    }
}