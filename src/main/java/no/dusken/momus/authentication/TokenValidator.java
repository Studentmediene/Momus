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

import no.dusken.momus.smmdb.SmmAbConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenValidator {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SmmAbConnector smmDbConnector;

    @Value("${smmDb.url}")
    String url;

    @Value("${smmDb.key}")
    String apiKey;

    public boolean validateToken(SmmAbToken token) {
        logger.info("Trying to validate token for {}", token.getUsername());
        String content = smmDbConnector.getTokenStatus(token);

        if (isValid(content)) {
            logger.info("User token was valid for user {}", token.getUsername());
            return true;
        } else {
            logger.warn("Invalid token for user {}", token.getUsername());
            return false;
        }
    }

    private boolean isValid(String content) {
        return content != null && content.equals("{\"verified\": true}");
    }


}
