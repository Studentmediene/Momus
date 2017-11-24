/*
 * Copyright 2016 Studentmediene i Trondheim AS
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

package no.dusken.momus.ldap;

import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.List;

@Service
public class LdapSyncer {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final int PAGE_SIZE = 1000;

    @Autowired
    LdapTemplate ldapTemplate;

    @Autowired
    PersonRepository personRepository;

    @Value("${ldap.syncEnabled}")
    private boolean enabled;

    @Value("${ldap.filter.user}")
    private String userFilter;

    /**
     * Will pull data from LDAP and update our local copy
     * 02:00 each day (second minute hour day month weekdays)
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void sync() {
        if (!enabled) {
            logger.info("Not syncing, it is disabled");
            return;
        }
        logger.info("Starting LDAP sync");

        long start = System.currentTimeMillis();

        syncAllPersonsFromLdap();

        long end = System.currentTimeMillis();
        long timeUsed = end - start;
        logger.info("Done syncing from LDAP, it took {}ms", timeUsed);
    }

    @PostConstruct
    public void startUp(){
        sync();
    }

    public void syncAllPersonsFromLdap() {
        List<Person> activePersons = searchForPersons("Brukere", true);

        List<Person> inactivePersons = searchForPersons("Sluttede", false);
        List<Person> tempLeavePersons = searchForPersons("Permisjon", false);

        logger.info("Number of users from LDAP: {}",
                activePersons.size() + inactivePersons.size() + tempLeavePersons.size());
        logger.info("Number of active: {}", activePersons.size());
        logger.info("Number of inactive: {}", inactivePersons.size());
        logger.info("Number of people on temporary leave: {}", tempLeavePersons.size());

        List<Person> allPersons = personRepository.findAll();

        long inactivated = allPersons.stream()
                .filter(Person::isActive)
                .filter(person -> !activePersons.contains(person))
                .peek(person -> {
                    person.setActive(false);
                    personRepository.saveAndFlush(person);
                })
                .count();
        logger.info("Made {} people inactive after syncing", inactivated);
    }

    /**
     * @param ou group to search in
     * @param active Whether or not the user should be flagged as active
     * @return A list of the found persons
     */
    private List<Person> searchForPersons(String ou, final boolean active){
        String base = String.format("ou=%s", ou);
        // For pagination
        PagedResultsDirContextProcessor processor = new PagedResultsDirContextProcessor(PAGE_SIZE, null);

        // For searching through subgroups
        SearchControls ctrl = new SearchControls();
        ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);

        PersonMapper personMapper = new PersonMapper(personRepository, active);

        List<Person> persons = new ArrayList<>();

        do{
            List<Person> result = ldapTemplate.search(base, userFilter, ctrl, personMapper, processor);
            persons.addAll(result);
            processor = new PagedResultsDirContextProcessor(PAGE_SIZE, processor.getCookie());
        }while (processor.getCookie().getCookie() != null);

        return persons;
    }

}
