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
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import no.dusken.momus.model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

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
        List<String> scopes = Arrays.asList("https://www.googleapis.com/auth/drive");


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
     * Creates a new Google Document for the article
     * and returns the file. Returns null if an error occured
     */
    public File createDocument(Article article) {
        File file = null;
        try {
            file = createFile(article);
            createPermission(file);
        } catch (IOException e) {
            logger.warn("Couldn't create Google Drive file for article id {}", article.getId());
        }

        return file;
    }

    private File createFile(Article article) throws IOException {
        File insertFile = new File();
        insertFile.setTitle(article.getName() + " - Momus.odt");
        insertFile.setMimeType("application/vnd.google-apps.document"); // Google Docs filetype

        File createdFile = drive.files().insert(insertFile).execute();

        logger.info("Created Google Drive file with id {} for article with id {}", createdFile.getId(), article.getId());
        return createdFile;
    }

    private void createPermission(File file) throws IOException {
        Permission permission = new Permission();
        permission.setRole("writer");
        permission.setType("anyone");
        permission.setValue("default");
        permission.setWithLink(true);

        drive.permissions().insert(file.getId(), permission).execute();
        logger.info("Created permissions for file with id {}", file.getId());
    }

    public void sync() {

    }
}
