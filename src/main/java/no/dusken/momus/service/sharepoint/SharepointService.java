package no.dusken.momus.service.sharepoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.microsoft.aad.adal4j.AuthenticationResult;

import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
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
import no.dusken.momus.service.sharepoint.models.DriveItem;
import no.dusken.momus.service.sharepoint.models.DriveItemChildren;

@Slf4j
@Service
public class SharepointService {
    @Value("${sharepoint.resource}")
    private String RESOURCE;

    @Value("${sharepoint.siteid}")
    private String SITE_ID;

    @Value("${sharepoint.driveid}")
    private String DRIVE_ID;

    private final String DRIVE_URL = "{resource}/v1.0/drives/{driveId}";
    private final String ITEM_URL = DRIVE_URL + "/items/{itemId}";
    private final String CHILDREN_URL = ITEM_URL + "/children";
    private final String CONTENT_URL = ITEM_URL + "/content";

    private RestTemplate restTemplate;

    private SharepointAuthenticator sharepointAuthenticator;
    private SharepointTextConverter textConverter;

    public SharepointService(SharepointAuthenticator sharepointAuthenticator) {
        this.sharepointAuthenticator = sharepointAuthenticator;
    }

    @PostConstruct
    public void setup() {
        AuthenticationResult authToken;
        try {
            authToken = sharepointAuthenticator.getAccessToken();
        } catch (Exception e) {
            log.error("Failed to authenticate with SharePoint {}", e);
            return;
        }

        this.restTemplate = setupRestTemplate(authToken.getAccessToken());
        this.textConverter = new SharepointTextConverter();
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
        Map<String, String> parameters = new HashMap<>();
        parameters.put("itemId", item.getId());
        Resource res = this.restTemplate.getForObject(CONTENT_URL, Resource.class, parameters);
        XWPFDocument doc = new XWPFDocument(res.getInputStream());
        OutputStream out = new ByteArrayOutputStream();

        XHTMLConverter.getInstance().convert(doc, out, XHTMLOptions.create());
        String html = out.toString();
        
        return this.textConverter.convert(html);
    }

    private RestTemplate setupRestTemplate(String authToken) {
        RestTemplate rest = new RestTemplate();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("driveId", DRIVE_ID);
        parameters.put("resource", RESOURCE);
        rest.setDefaultUriVariables(parameters);
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