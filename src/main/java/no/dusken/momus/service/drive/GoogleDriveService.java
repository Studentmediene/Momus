/*
 * Copyright 2016 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.dusken.momus.service.drive;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Change;
import com.google.api.services.drive.model.ChangeList;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.model.Article;
import no.dusken.momus.service.ArticleService;
import no.dusken.momus.service.KeyValueService;
import no.dusken.momus.service.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
@Slf4j
public class GoogleDriveService {

    @Value("${drive.syncEnabled}")
    private boolean enabled;

    @Value("${drive.appName}")
    private String appName;

    @Value("${drive.email}")
    private String email;

    @Autowired
    KeyValueService keyValueService;

    @Autowired
    GoogleDocsTextConverter googleDocsTextConverter;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleService articleService;

    private Drive drive = null;

    @PostConstruct
    private void setup() {
        if (!enabled) {
            log.info("Not setting up Google Drive");
            return;
        }

        log.info("Setting up Google Drive");


        GoogleCredential credentials;
        HttpTransport httpTransport;
        java.io.File keyFile;
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        List<String> scopes = Arrays.asList("https://www.googleapis.com/auth/drive"); // We want full access


        try {
            keyFile = new ClassPathResource("googlekey.p12").getFile();
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            credentials = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setServiceAccountId(email)
                    .setServiceAccountPrivateKeyFromP12File(keyFile)
                    .setServiceAccountScopes(scopes)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            log.error("Couldn't create Google Drive credentials: ", e);
            return;
        }

        drive = new Drive.Builder(httpTransport, jsonFactory, credentials)
                .setApplicationName(appName)
                .build();

        log.info("Successfully set up Google Drive");
    }


    /**
     * Creates a new Google Document for the name
     * and returns the file. Returns null if an error occurred
     */
    public File createDocument(String name) {
        File file = null;
        try {
            file = createFile(name);
            createPermission(file);
        } catch (IOException e) {
            log.error("Couldn't create Google Drive file for article", e);
        }

        return file;
    }

    private File createFile(String name) throws IOException {
        File insertFile = new File();
        insertFile.setTitle(name + " - Momus");
        insertFile.setMimeType("application/vnd.google-apps.document"); // Google Docs filetype

        File createdFile = drive.files().insert(insertFile).execute();

        log.info("Created Google Drive file with id {} for article with name {}", createdFile.getId(), name);
        return createdFile;
    }

    /**
     * Creates a permission for the Google Docs file that allows
     * anyone to view it if they have the correct link
     */
    private void createPermission(File file) throws IOException {
        Permission permission = new Permission();
        permission.setRole("writer");
        permission.setType("anyone");
        permission.setValue("default");
        permission.setWithLink(true);

        drive.permissions().insert(file.getId(), permission).execute();
        log.info("Created permissions for Google Drive file with id {}", file.getId());
    }


    /**
     * Will pull data from Google Drive and update our local copy
     * every minute (each time the seconds are "0")
     *
     * (second minute hour day month weekdays)
     */
    @Scheduled(cron = "0 * * * * *")
    public void sync() {
        if (!enabled) {
            log.info("Not syncing Google Drive");
            return;
        }
        log.debug("Starting Google Drive sync");

        Set<String> modifiedFileIds = findModifiedFileIds();
        if (modifiedFileIds.size() == 0) {
            return;
        }

        List<Article> articles = articleRepository.findByGoogleDriveIdIn(modifiedFileIds);
        for (Article article : articles) {
            updateContentFromDrive(article);
            articleService.updateArticleContent(article);
        }

        log.debug("Done syncing, updated {} articles", articles.size());
    }

    /**
     * Returns the (up to) 200 next changed files from Google Drive
     * The IDs returned are Google Drive file IDs.
     */
    private Set<String> findModifiedFileIds() {
        Set<String> modifiedFileIds = new HashSet<>();

        long oldLatestChange = keyValueService.getValueAsLong("DRIVE_LATEST", 0L);
        long latestChange = oldLatestChange;

        try {
            Drive.Changes.List request =  drive.changes().list();
            request.setStartChangeId(oldLatestChange + 1);
            request.setIncludeSubscribed(false);
            request.setMaxResults(200);

            ChangeList results = request.execute();
            List<Change> changes = results.getItems();

            for (Change change : changes) {
                modifiedFileIds.add(change.getFileId());
                latestChange = change.getId();
                log.debug("Change found, id {}, fileid: {}", change.getId(), change.getFileId());
            }

            if (latestChange != oldLatestChange) {
                keyValueService.setValue("DRIVE_LATEST", latestChange);
            }


        } catch (IOException e) {
            log.warn("Couldn't get changed file IDs from Google Drive", e); // may happen once in a while, not that interesting
        }

        return modifiedFileIds;
    }

    /**
     * Fetches the new content from Google Drive as HTML, and sends
     * it to the googleDocsTextConverter
     */
    private void updateContentFromDrive(Article article) {
        String downloadUrl = "https://docs.google.com/feeds/download/documents/export/Export?id=" + article.getGoogleDriveId() + "&exportFormat=html";

        try {
            // Read data, small hack to convert the stream to a string
            InputStream inputStream = drive.getRequestFactory().buildGetRequest(new GenericUrl(downloadUrl)).execute().getContent();
            Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
            String content = s.hasNext() ? s.next() : "";
            log.info("Got new content:\n{}", content);

            String convertedContent = googleDocsTextConverter.convert(content);
            article.setContent(convertedContent);
        } catch (IOException e) {
            log.error("Couldn't get content for article", e);
            // Let the content remain as it was
        }
    }
}
