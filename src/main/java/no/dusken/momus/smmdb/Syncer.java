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

import no.dusken.momus.mapper.GroupMapper;
import no.dusken.momus.mapper.PersonMapper;
import no.dusken.momus.model.Group;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.GroupRepository;
import no.dusken.momus.service.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Syncer {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SmmAbConnector smmDbConnector;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    PersonMapper personMapper;

    @Autowired
    GroupMapper groupMapper;

    /**
     * Will pull data from SmmDb and update our local copy
     * 02:00 each day (second minute hour day month weekdays)
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void sync() {
        logger.info("Starting SmmDb sync");

        boolean syncGroupSuccess = syncGroups();
        // Only sync people if the groups worked, or else we may run into issues
        if (syncGroupSuccess) {
            boolean syncPersonSuccess = syncPeople();
            if (syncPersonSuccess) {
                logger.info("Done syncing from SmmDb");
            }
        }
    }

    private boolean syncGroups() {
        String groupsJsonString = smmDbConnector.getAllGroups();
        List<Group> smmDbGroups = groupMapper.groupsFromJson(groupsJsonString, "objects");

        if (smmDbGroups == null || smmDbGroups.size() == 0) {
            // Something probably didn't work as it should
            return false;
        }

        // Delete stuff that is in our database but not in the latest data
        List<Group> oldGroups = groupRepository.findAll();
        oldGroups.removeAll(smmDbGroups);
        groupRepository.delete(oldGroups);

        groupRepository.save(smmDbGroups);
        return true;
    }

    private boolean syncPeople() {
        String personsJsonString = smmDbConnector.getAllUsers();
        List<Person> smmDbPersons = personMapper.personsFromJson(personsJsonString, "objects");

        if (smmDbPersons == null || smmDbPersons.size() == 0) {
            return false;
        }

        List<Person> oldPersons = personRepository.findAll();
        oldPersons.removeAll(smmDbPersons);
        personRepository.delete(oldPersons);

        personRepository.save(smmDbPersons);
        return true;
    }
}
