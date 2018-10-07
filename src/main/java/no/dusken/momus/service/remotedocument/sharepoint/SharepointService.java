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

import javax.annotation.PostConstruct;

import com.microsoft.aad.adal4j.AuthenticationResult;

import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.model.Article;
import no.dusken.momus.service.ArticleService;
import no.dusken.momus.service.KeyValueService;
import no.dusken.momus.service.remotedocument.RemoteDocumentService;
import no.dusken.momus.service.remotedocument.sharepoint.models.DriveItem;
import no.dusken.momus.service.remotedocument.sharepoint.models.DriveItemChildren;
import no.dusken.momus.service.remotedocument.sharepoint.models.ItemDeltaList;
import no.dusken.momus.service.repository.ArticleRepository;

@Slf4j
@Service
public class SharepointService implements RemoteDocumentService {

    @Value("${sharepoint.syncEnabled}")
    private boolean enabled;

    @Value("${sharepoint.resource}")
    private String RESOURCE;

    @Value("${sharepoint.siteid}")
    private String SITE_ID;

    @Value("${sharepoint.driveid}")
    private String DRIVE_ID;

    private Map<String, String> defaultParameters;

    private boolean isInitialized = false;

    private final String DRIVE_URL = "{resource}/v1.0/drives/{driveId}";
    private final String ITEM_URL = DRIVE_URL + "/items/{itemId}";
    private final String CHILDREN_URL = ITEM_URL + "/children";
    private final String CONTENT_URL = ITEM_URL + "/content";
    private final String DELTA_URL_NO_TOKEN = DRIVE_URL + "/root/delta";

    private final String DELTA_URL_KEY = "SHAREPOINT_DELTA_URL";

    private RestTemplate restTemplate;

    private SharepointAuthenticator sharepointAuthenticator;
    private SharepointTextConverter textConverter;

    private KeyValueService keyValueService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

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
        parameters.put("driveId", DRIVE_ID);
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

    public void sync() {
        if(!enabled) {
            return;
        }
        log.debug("Starting Sharepoint sync");

        try {
            List<DriveItem> deltas = getDeltas();
            applyDeltas(deltas);
        } catch (URISyntaxException e) {
            log.error("Could not get deltas from Sharepoint", e);
        }
    }

    public DriveItem getRootDriveItem() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("itemId", "root");
        return this.restTemplate.getForObject(ITEM_URL, DriveItem.class, parameters);
    }

    public DriveItem getByItemId(String itemId) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("itemId", itemId);
        return this.restTemplate.getForObject(ITEM_URL, DriveItem.class, parameters);
    }

    public List<DriveItem> getChildren(DriveItem item) {
        if(item.getFolder() == null) {
            throw new IllegalArgumentException(
                String.format("Can't get children of an item %s, it is not a folder", item.getName())
            );
        }
        log.info("Getting children for item {}", item.getId());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("itemId", item.getId());
        DriveItemChildren children = this.restTemplate.getForObject(
            CHILDREN_URL,
            DriveItemChildren.class,
            parameters
        );
        return children.getValue();
    }

    public DriveItem createDocument(String name) {
        log.info("Creating sharepoint file with name {}", name);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("itemId", "root");

        String req = "{\"name\": \"" + name + ".docx\",\"file\": {}}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(req, headers);

        try {
            return this.restTemplate.postForObject(
                CHILDREN_URL,
                entity,
                DriveItem.class,
                parameters
            );
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

    public String getItemContentAsHtml(DriveItem item) throws IOException {
        log.debug(item.getId());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("itemId", item.getId());
        Resource res = this.restTemplate.getForObject(CONTENT_URL, Resource.class, parameters);
        XWPFDocument doc = new XWPFDocument(res.getInputStream());
        OutputStream out = new ByteArrayOutputStream();

        XHTMLConverter.getInstance().convert(doc, out, XHTMLOptions.create());
        String html = out.toString();
        
        return this.textConverter.convert(html);
    }

    public ServiceName getServiceName() {
        return ServiceName.SHAREPOINT;
    }

    private List<DriveItem> getDeltas() throws URISyntaxException {
        String deltaLink = keyValueService.getValue(DELTA_URL_KEY);
        URI url = this.restTemplate.getUriTemplateHandler().expand(DELTA_URL_NO_TOKEN, this.defaultParameters);
        if(deltaLink != null) {
            url = new URI(deltaLink);
        }
        
        ItemDeltaList deltaList;
        List<DriveItem> deltas = new ArrayList<>();

        do {
            log.debug("URL: {} ", url);
            deltaList = this.restTemplate.getForObject(url, ItemDeltaList.class);
            deltas.addAll(deltaList.getValue());
            if (deltaList.getNextLink() != null) {
                url = new URI(deltaList.getNextLink());
            }
        } while(deltaList.getNextLink() != null);

        log.debug("Got deltas: {} nextLink: {} deltaLink: {}", deltaList.getValue().size(), deltaList.getNextLink(), deltaList.getDeltaLink());

        keyValueService.setValue(DELTA_URL_KEY, deltaList.getDeltaLink());

        return deltaList.getValue();
    }

    private void applyDeltas(List<DriveItem> deltas) {
        deltas.forEach(delta -> {
            try {
                if(delta.getDeleted() != null) {
                    log.debug("Item {} was deleted", delta.getName());
                    return;
                }
                String content = getItemContentAsHtml(delta);
                Article article = articleRepository.findByRemoteId(delta.getId());
                if(article == null) {
                    return;
                }
                articleService.updateArticleContent(article.getId(), content);
            } catch (IOException e) {
                e.printStackTrace();
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