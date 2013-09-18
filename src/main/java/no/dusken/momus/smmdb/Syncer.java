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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import no.dusken.momus.model.Group;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.GroupRepository;
import no.dusken.momus.service.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class Syncer {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SmmdbConnector smmdbConnector;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    PersonRepository personRepository;

    public void sync() {
        syncGroups();
        syncPeople();
    }

    private void syncGroups() {
        String groupsJsonString = smmdbConnector.getAllGroups();
        List<Group> smmdbGroups = groupsFromJson(groupsJsonString);

        List<Group> allGroups = groupRepository.findAll(new Sort(Sort.Direction.ASC, "id"));

        System.out.println("asdasd");

    }

    private void syncPeople() {
        //To change body of created methods use File | Settings | File Templates.
    }

    private List<Group> groupsFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ObjectReader reader = mapper.reader().withRootName("objects");

        
        List<Group> groups = Collections.EMPTY_LIST;
        try {

            JsonParser parser = mapper.getFactory().createParser(json);
            JsonNode root = mapper.readTree(parser);
            JsonParser groupsObject = root.path("objects").traverse();

            groups = mapper.readValue(groupsObject, new TypeReference<List<Group>>() {});

        } catch (IOException e) {
            logger.warn("ops: {}", e);
        }

        return groups;


    }
}
