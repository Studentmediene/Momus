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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.dusken.momus.exceptions.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class SmmdbToken {

    Logger logger = LoggerFactory.getLogger(getClass());

    private String username;
    private Long id;
    private String jsonText;

    public SmmdbToken() {

    }

    public SmmdbToken(String jsonText) {
        this.jsonText = jsonText;
        readJson();
    }

    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    private void readJson() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> values = null;
        try {
            values = mapper.readValue(new JsonFactory().createParser(jsonText), Map.class);
        } catch (IOException e) {
            logger.warn("Invalid JSON data: {}", jsonText);
            throw new RestException("Invalid JSON for token", HttpServletResponse.SC_BAD_REQUEST);
        }

        username = (String) values.get("username");
        id = Long.valueOf((Integer) values.get("userid"));
    }
}
