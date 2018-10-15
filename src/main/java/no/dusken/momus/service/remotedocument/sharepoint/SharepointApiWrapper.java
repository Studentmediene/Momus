package no.dusken.momus.service.remotedocument.sharepoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.microsoft.aad.adal4j.AuthenticationResult;

import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.env.Environment;
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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.service.remotedocument.sharepoint.models.DeltaItem;
import no.dusken.momus.service.remotedocument.sharepoint.models.DriveItem;
import no.dusken.momus.service.remotedocument.sharepoint.models.ItemDeltaList;
import no.dusken.momus.service.remotedocument.sharepoint.models.ListItemFields;
import no.dusken.momus.service.remotedocument.sharepoint.models.SpList;
import no.dusken.momus.service.remotedocument.sharepoint.models.User;

@Slf4j
@Service
public class SharepointApiWrapper {
    private final Map<String, String> defaultParams;

    private final String ROOT_URL = "{resource}/v1.0";
    private final String DRIVE_URL = ROOT_URL + "/drives/{driveId}";
    private final String ARTICLE_ITEM_URL = DRIVE_URL + "/items/{itemId}?expand=listItem";
    private final String CREATE_ARTICLE_URL = DRIVE_URL + "/items/root:/{fileName}:/content";
    private final String ARTICLE_CONTENT_URL = DRIVE_URL + "/items/{itemId}/content";
    private final String DELTA_URL_NO_TOKEN = DRIVE_URL + "/root/delta";

    private final String USER_URL = ROOT_URL + "/users/{userId}";

    private final String LISTS_URL = ROOT_URL + "/sites/{siteId}/lists";
    private final String LIST_URL = LISTS_URL + "/{listId}?select=id&expand=columns(select=name)";
    private final String PATCH_ITEM_URL = LISTS_URL + "/{listId}/items/{itemId}/fields";

    private AuthenticationResult authToken;

    private final SharepointAuthenticator authenticator;
    private final SharepointTextConverter textConverter;
    private final RestTemplate restTemplate;
    
    public SharepointApiWrapper(SharepointAuthenticator authenticator, Environment env) {
        this.authenticator = authenticator;

        Map<String, String> defaultParams = new HashMap<>();
        defaultParams.put("siteId", env.getProperty("sharepoint.siteid"));
        defaultParams.put("driveId", env.getProperty("sharepoint.driveid"));
        defaultParams.put("resource", env.getProperty("sharepoint.resource"));
        defaultParams.put("listId", env.getProperty("sharepoint.listid"));
        this.defaultParams = defaultParams;
        this.restTemplate = setupRestTemplate(defaultParams);

        this.textConverter = new SharepointTextConverter();
    }

    public void authenticate() throws Exception {
        this.authToken = this.authenticator.getAccessToken();
    }

    public DriveItem getDriveItem(String id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("itemId", id);
        DriveItem res = this.restTemplate.getForObject(ARTICLE_ITEM_URL, DriveItem.class, parameters);
        return res;
    }

    public SpList getArticlesList() {
        Map<String, String> parameters = new HashMap<>();
        SpList list = this.restTemplate.getForObject(LIST_URL, SpList.class, parameters);
        log.debug("{}", list);
        return list;
    }

    public DriveItem createDocument(String name) throws IOException {
        log.info("Creating sharepoint file with name {}", name);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("fileName", name + ".docx");


        HttpHeaders headers = new HttpHeaders();
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

    public User getUser(String id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userId", id);
        User res = this.restTemplate.getForObject(USER_URL, User.class, parameters);
        return res;
    }

    public void setMomusLink(DriveItem item, String text) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("itemId", item.getListItem().getId());

        String req = "{\"" + ListItemFields.MOMUSLINK_FIELD_NAME + "\": \"" + text + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(req, headers);
        this.restTemplate.patchForObject(PATCH_ITEM_URL, entity, String.class, parameters);
    }

    public String getItemContentAsHtml(DriveItem item) throws IOException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("itemId", item.getId());
        Resource res = this.restTemplate.getForObject(ARTICLE_CONTENT_URL, Resource.class, parameters);
        XWPFDocument doc = new XWPFDocument(res.getInputStream());
        OutputStream out = new ByteArrayOutputStream();

        XHTMLConverter.getInstance().convert(doc, out, XHTMLOptions.create());
        String html = out.toString();

        return this.textConverter.convert(html);
    }

    public ItemDeltaList getDeltas(String deltaLink) throws URISyntaxException {
        URI url = this.restTemplate.getUriTemplateHandler().expand(DELTA_URL_NO_TOKEN, this.defaultParams);
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
            return null;
        }

        // We only want the valid deltas for us, which means no folders, not deleted
        // And we also only need one delta per changed article for every sync, so we make it a set.
        Set<DeltaItem> validDeltas = deltas.stream()
                .filter(item -> item.getFolder() == null && item.getDeleted() == null)
                .collect(Collectors.toSet());

        ItemDeltaList deltaList = new ItemDeltaList();
        deltaList.setValue(new ArrayList<DeltaItem>(validDeltas));
        deltaList.setDeltaLink(nextDeltaLink);
        return deltaList;
    }

    private RestTemplate setupRestTemplate(Map<String, String> defaultParams) {
        RestTemplate rest = new RestTemplate();

        rest.setDefaultUriVariables(defaultParams);

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(10000);

        rest.setRequestFactory(requestFactory);
        rest.setInterceptors(Collections.singletonList(new ClientHttpRequestInterceptor(){
        
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                    throws IOException {
                // If we have no token, we just try to execute, though it will probably fail. Just let it
                if(authToken == null) {
                    return execution.execute(request, body);
                }
                // Our token has expired, so we refresh it!
                if(authToken.getExpiresOnDate().before(new Date())) {
                    try {
                        authToken = authenticator.refreshToken(authToken);
                    } catch (Exception e) {
                        log.error("Failed to refresh Sharepoint token {}", e);
                    }
                }
                request.getHeaders().set("Authorization", "Bearer " + authToken.getAccessToken());
                return execution.execute(request, body);
            }
        }));

        return rest;
    }
}