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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import no.dusken.momus.model.Group;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.GroupRepository;
import no.dusken.momus.service.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    private ObjectMapper mapper;

    public Syncer() {
        mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Will pull data from Smmdb and update our local copy
     */
    @Scheduled(cron = "0 0 2 * * *") // 02:00 each day (second minute hour day month weekdays)
    public void sync() {
        logger.info("Starting smmdb sync");

        boolean syncGroupSuccess = syncGroups();
        // Only sync people if the groups worked, or else we may run into issues
        if (syncGroupSuccess) {
            boolean syncPersonSuccess = syncPeople();
            if (syncPersonSuccess) {
                logger.info("Done syncing from smmdb");
            }
        }
    }

    private boolean syncGroups() {
        String groupsJsonString = smmdbConnector.getAllGroups();
        List<Group> smmdbGroups = groupsFromJson(groupsJsonString);

        if (smmdbGroups == null || smmdbGroups.size() == 0) {
            // Something probably didn't work as it should
            return false;
        }

        // Delete stuff that is in our database but not in the latest data
        List<Group> oldGroups = groupRepository.findAll();
        oldGroups.removeAll(smmdbGroups);
        groupRepository.delete(oldGroups);

        groupRepository.save(smmdbGroups);
        return true;
    }

    private boolean syncPeople() {
        String personsJsonString = smmdbConnector.getAllUsers();
        List<Person> smmdbPersons = personsFromJson(personsJsonString);

        if (smmdbPersons == null || smmdbPersons.size() == 0) {
            return false;
        }

        List<Person> oldPersons = personRepository.findAll();
        oldPersons.removeAll(smmdbPersons);
        personRepository.delete(oldPersons);

        personRepository.save(smmdbPersons);
        return true;
    }

    private List<Group> groupsFromJson(String json) {
        List<Group> groups = null;
        try {

            JsonParser parser = mapper.getFactory().createParser(json);
            JsonNode root = mapper.readTree(parser);
            JsonParser groupsObject = root.path("objects").traverse();

            groups = mapper.readValue(groupsObject, new TypeReference<List<Group>>() {});
        } catch (IOException e) {
            logger.warn("Couldn't create groups from json: {}", e);
        }

        return groups;
    }

    private List<Person> personsFromJson(String json) {
        List<Person> persons = null;
        try {

            JsonParser parser = mapper.getFactory().createParser(json);
            JsonNode root = mapper.readTree(parser);
            JsonParser personsObject = root.path("objects").traverse();

            persons = mapper.readValue(personsObject, new TypeReference<List<Person>>() {});
        } catch (IOException e) {
            logger.warn("Couldn't create persons from json: {}", e);
        }

        return persons;
    }

}
