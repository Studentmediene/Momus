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

package no.dusken.momus.service.publicapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("apiAccess")
public class ApiAccess {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${publicapi.users}")
    String usersText;

    private List<String> users;

    @PostConstruct
    private void createUsers() {
        System.out.println("lalala");
        String[] credentials = usersText.split(";");

        users = new ArrayList<>(Arrays.asList(credentials));
    }

    public boolean authorizeRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        logger.info("New API request with authHeader '{}' for URL {} from {}", authHeader, request.getRequestURL(), request.getRemoteAddr());

        if (authHeader == null || authHeader.isEmpty()) {
            logger.warn("API request failed, no auth header");
            return false;
        }

        String credentials = extractCredentials(authHeader);
        if (credentials == null) {
            logger.warn("API request failed, couldn't extract credentials");
            return false;
        }

        if (users.contains(credentials)) {
            logger.info("API request allowed");
            return true;
        } else {
            logger.warn("API request failed, not valid username or password");
            return false;
        }

    }

    private String extractCredentials(String authHeader) {
        String encodedPart = authHeader.substring(6);

        try {
            byte[] encodedBytes = encodedPart.getBytes("UTF-8");
            byte[] decodedBytes = Base64.decode(encodedBytes);

            return new String(decodedBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }


    }



//    private static class
}
