package no.dusken.momus.service.remotedocument.sharepoint;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.model.Article;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.ArticleService;
import no.dusken.momus.service.KeyValueService;
import no.dusken.momus.service.remotedocument.RemoteDocumentService;
import no.dusken.momus.service.remotedocument.sharepoint.models.DeltaItem;
import no.dusken.momus.service.remotedocument.sharepoint.models.DriveItem;
import no.dusken.momus.service.remotedocument.sharepoint.models.ItemDeltaList;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.PersonRepository;

@Slf4j
@Service("remoteDocumentService")
public class SharepointService implements RemoteDocumentService {
    private final String DELTA_URL_KEY = "SHAREPOINT_DELTA_URL";

    @Value("${sharepoint.enabled}")
    private boolean enabled;


    @Value("${sharepoint.sync}")
    private boolean shouldSync;

    private final SharepointApiWrapper apiWrapper;
    private final KeyValueService keyValueService;
    private final PersonRepository personRepository;
    private final Environment env;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    public SharepointService(
        KeyValueService keyValueService,
        PersonRepository personRepository,
        SharepointApiWrapper apiWrapper,
        Environment env) {
        this.keyValueService = keyValueService;
        this.personRepository = personRepository;
        this.apiWrapper = apiWrapper;
        this.env = env;
    }

    @PostConstruct
    public void setup() {
        if (!enabled) {
            log.info("Not setting up Sharepoint");
            return;
        }

        log.info("Setting up Sharepoint");

        try {
            apiWrapper.authenticate();
        } catch (Exception e) {
            log.error("Failed to authenticate with Sharepoint! {}", e);
            return;
        }

        sync();
    }

    public ServiceName getServiceName() {
        return ServiceName.SHAREPOINT;
    }

    public DriveItem createDocument(String name) throws IOException {
        return apiWrapper.createDocument(name);
    }

    public void addRemoteMetadata(Article article) {
        DriveItem item = apiWrapper.getDriveItem(article.getRemoteId());
        StringBuilder link = new StringBuilder();
        if(Arrays.asList(env.getActiveProfiles()).contains("dev")) {
            link.append("http://localhost:8080");
        } else {
            link.append("https://momus.smint.no");
        }
        link.append("/#/artikler/").append(article.getId());
        apiWrapper.setMomusLink(item, link.toString());
    }

    @Scheduled(cron = "30 * * * * *")
    public void sync() {
        if(!shouldSync) {
            return;
        }
        log.debug("Starting Sharepoint sync");

        try {
            String deltaLink = keyValueService.getValue(DELTA_URL_KEY);
            ItemDeltaList deltas;
            try {
                deltas = apiWrapper.getDeltas(deltaLink);
            } catch(HttpClientErrorException e) {
                if(e.getStatusCode() == HttpStatus.GONE) {
                    // This happens sometimes. We reset the delta link and re-add all deltas. Might be inefficient, but should not happen often
                    log.warn("Sharepoint reported 410 Gone on delta link, resetting and resyncing");
                    keyValueService.setValue(DELTA_URL_KEY, null);
                    sync();
                    return;
                } else {
                    log.warn("Sharepoint failed to get delta, with code {}", e.getStatusCode());
                    return;
                }
            }
            if(deltas == null) {
                log.info("No changes from Sharepoint");
            } else {
                log.info("Got deltas: {}", deltas.getValue().size());
                applyDeltas(deltas.getValue());

                keyValueService.setValue(DELTA_URL_KEY, deltas.getDeltaLink());
            }
        } catch (URISyntaxException e) {
            log.error("Could not get deltas from Sharepoint", e);
        }

        log.debug("Sharepoint sync finished");
    }

    private void applyDeltas(List<DeltaItem> deltas) {
        deltas.forEach(delta -> {
            try {
                Article article = articleRepository.findByRemoteId(delta.getId());
                if(article == null) {
                    log.debug("Item {} was not found in the database, skipping", delta.getId());
                    return;
                }

                DriveItem item = apiWrapper.getDriveItem(delta.getId());
                String content = apiWrapper.getItemContentAsHtml(item);

                String username = apiWrapper.getUser(item.getLastModifiedUserId()).getUsername();
                Person dbPerson = personRepository.findByUsername(username);
                log.debug("Article {} was changed by: {}", item.getName(), dbPerson);
                articleService.updateArticleContent(article.getId(), content, dbPerson);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (HttpClientErrorException e) {
                log.error("Error applying delta {} {} {}", delta, e.getStatusCode(), e.getResponseBodyAsString());
            }
        });
    }
}