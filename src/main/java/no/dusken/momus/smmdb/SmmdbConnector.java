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

package no.dusken.momus.smmdb;

import no.dusken.momus.authentication.SmmdbToken;
import no.dusken.momus.exceptions.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

@Service
public class SmmdbConnector {

    @Value("${smmdb.url}")
    String smmdbUrl;

    @Value("${smmdb.key}")
    String apiKey;

    Logger logger = LoggerFactory.getLogger(getClass());

    public String getTokenStatus(SmmdbToken token) {
        String service = "ticket/verify";
        String params = "ticket=" + encode(token.getJsonText());

        return getData(service, params);
    }

    public String getAllUsers() {
        String service = "user";

        return getData(service, null);
    }

    private String getData(String service, String params) {
        String fullUrl = smmdbUrl + "/" + service + "?key=" + apiKey;
        if (params != null) {
            fullUrl += "&" + params;
        }

        String content = "";
        try {
            HttpURLConnection connection = createConnection(fullUrl);
            content = getContentFromConnection(connection);
            logger.debug("Content is: {}", content);
        } catch (Exception e) {
            logger.warn("Something went wrong connecting to Smmdb: {}", e);
            throw new RestException("Couldn't connect to Smmdb", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return content;
    }

    private HttpURLConnection createConnection(String url) throws IOException {
        logger.debug("Trying to access: {}", url);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Momus/1.0 (Mats)");
        connection.connect();

        return connection;
    }

    private String getContentFromConnection(HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() != HttpServletResponse.SC_OK) {
            logger.warn("Smmdb responsecode: {}", connection.getResponseCode());
            return "";
        }

        InputStream inputStream = connection.getInputStream();
        Scanner s = new Scanner(inputStream);
        // Hack to read whole stream at once, makes the whole content "next"
        s.useDelimiter("\\A");

        // May be empty
        return s.hasNext() ? s.next() : "";
    }


    private String encode(String str) {
        String result = null;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Should probably never happen...
            logger.warn("UTF-8 not accepted, trace: {}", e);
        }
        return result;
    }

}
