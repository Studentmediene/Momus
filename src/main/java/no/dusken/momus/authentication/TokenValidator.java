/*
 * Copyright 2013 Studentmediene i Trondheim AS
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

package no.dusken.momus.authentication;

import no.dusken.momus.exceptions.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Service
public class TokenValidator {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${smmdb.url}")
    String url;

    public boolean validateToken(SmmdbToken token) {
        logger.info("Trying to validate token for {}", token.getUsername());
        String content;
        try {
            HttpURLConnection connection = createConnection();
            content = getContentFromConnection(connection);
        } catch (IOException e) {
            logger.warn("Something went wrong connecting to Smmdb: {}", e);
            throw new RestException("Couldn't connect to Smmdb", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        if (isValid(content)) {
            logger.info("User token was valid for user {}", token.getUsername());
            return true;
        } else {
            logger.warn("Invalid token for user {}", token.getUsername());
            return false;
        }
    }

    private HttpURLConnection createConnection() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Momus/1.0 (Mats)");
        connection.connect();

        return connection;
    }

    private String getContentFromConnection(HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        Scanner s = new Scanner(inputStream);
        // Hack to read whole stream at once
        s.useDelimiter("\\A");
        // May be empty
        return s.hasNext() ? s.next() : "";
    }

    private boolean isValid(String content) {
        if (content == null) {
            return false;
        }

        //TODO: Something
        return true;
    }


}
