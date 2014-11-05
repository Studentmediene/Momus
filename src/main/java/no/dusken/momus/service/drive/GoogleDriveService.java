/*
 * Copyright 2014 Studentmediene i Trondheim AS
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
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Change;
import com.google.api.services.drive.model.ChangeList;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GoogleDriveService {

    @Value("${drive.syncEnabled}")
    private boolean enabled;

    @Value("${drive.appName}")
    private String appName;

    @Value("${drive.email}")
    private String email;

    private Drive drive = null;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @PostConstruct
    private void setup() {
        if (!enabled) {
            logger.info("Not setting up Google Drive");
            return;
        }

        logger.info("Setting up Google Drive");


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
            logger.warn("Couldn't create Google Drive credentials: ", e);
            return;
        }

        drive = new Drive.Builder(httpTransport, jsonFactory, credentials)
                .setApplicationName(appName)
                .build();

        logger.info("Successfully set up Google Drive");
    }


    /**
     * Creates a new Google Document for the name
     * and returns the file. Returns null if an error occurred
     *
     * @param name
     */
    public File createDocument(String name) {
        File file = null;
        try {
            file = createFile(name);
            createPermission(file);
        } catch (IOException e) {
            logger.warn("Couldn't create Google Drive file for article  {}", name);
        }

        return file;
    }

    private File createFile(String name) throws IOException {
        File insertFile = new File();
        insertFile.setTitle(name + " - Momus");
        insertFile.setMimeType("application/vnd.google-apps.document"); // Google Docs filetype

        File createdFile = drive.files().insert(insertFile).execute();

        logger.info("Created Google Drive file with id {} for article with name {}", createdFile.getId(), name);
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
        logger.info("Created permissions for Google Drive file with id {}", file.getId());
    }


    /**
     * Will pull data from Google Drive and update our local copy
     * every minute ("each time the seconds are 0")
     *
     * (second minute hour day month weekdays)
     */
    @Scheduled(cron = "0 * * * * *")
    public void sync() {
        if (!enabled) {
            logger.debug("Not syncing Google Drive");
            return;
        }
        logger.debug("Starting Google Drive sync");

        Set<String> modifiedFileIds = findModifiedFileIds();


        // Get the articles having these IDs



    }

    /**
     * Returns the 200 next changes from Google Drive
     * The IDs returned are Google Drive file IDs.
     */
    private Set<String> findModifiedFileIds() {
        Set<String> modifiedFileIds = new HashSet<>();

        try {
            Drive.Changes.List request =  drive.changes().list();
            request.setIncludeSubscribed(false);
            request.setMaxResults(200);
            request.setStartChangeId(146L);

            ChangeList results = request.execute();
            List<Change> changes = results.getItems();

            for (Change change : changes) {
                modifiedFileIds.add(change.getFileId());
            }


        } catch (IOException e) {
            logger.error("Couldn't get changed file IDs from Google Drive", e);
        }

        return modifiedFileIds;
    }
}
