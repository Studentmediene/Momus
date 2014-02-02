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

package no.dusken.momus.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import no.dusken.momus.model.Group;
import no.dusken.momus.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GroupMapper {

    Logger logger = LoggerFactory.getLogger(getClass());

    private ObjectMapper mapper;

    public GroupMapper() {
        mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Group groupFromJson(String json) {
        Group group = null;
        try {
            group = mapper.readValue(json, Group.class);
        } catch (IOException e) {
            logger.warn("Couldn't create single group from json: {}", e);
        }
        return group;
    }

    /**
     * Converts JSON to a list of groups. Root element specify if the groups are not the
     * top element of the json. For instance for this JSON one should specify that the groups
     * are inside "groupObjects":
     * {
     *  "someValue": 33,
     *  "groupObjects": [list of groups]
     * }
     */
    public List<Group> groupsFromJson(String json, String rootElement) {
        List<Group> groups = null;
        try {

            JsonParser parser = mapper.getFactory().createParser(json);

            if (rootElement != null) { // go inside the specified root element
                JsonNode root = mapper.readTree(parser);
                parser = root.path(rootElement).traverse();
            }

            groups = mapper.readValue(parser, new TypeReference<List<Group>>() {});
        } catch (IOException e) {
            logger.warn("Couldn't create groups from json: {}", e);
        }

        return groups;
    }

    public List<Group> groupsFromJson(String json) {
        return groupsFromJson(json, null);
    }

}
