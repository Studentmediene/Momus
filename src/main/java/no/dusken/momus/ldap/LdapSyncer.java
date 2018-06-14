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

import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.authorization.Role;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LdapSyncer {

    private final int PAGE_SIZE = 1000;

    private final String USER_FILTER = "(&(objectClass=user)(!(objectClass=inetOrgPerson)))";

    @Autowired
    LdapTemplate ldapTemplate;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    Environment env;

    @Value("${ldap.syncEnabled}")
    private boolean enabled;

    private Map<String, Role> groupToRole = new HashMap<>();

    /**
     * Will pull data from LDAP and update our local copy
     * 02:00 each day (second minute hour day month weekdays)
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void sync() {
        if (!enabled) {
            log.info("Not syncing, it is disabled");
            return;
        }
        log.info("Starting LDAP sync");

        long start = System.currentTimeMillis();

        syncAllPersonsFromLdap();

        long end = System.currentTimeMillis();
        long timeUsed = end - start;
        log.info("Done syncing from LDAP, it took {}ms", timeUsed);
    }

    @PostConstruct
    public void startUp(){
        groupToRole.put(env.getProperty("roles.admin"), Role.ROLE_ADMIN);
        groupToRole.put(env.getProperty("roles.illustrator"), Role.ROLE_ILLUSTRATOR);
        sync();
    }

    public void syncAllPersonsFromLdap() {
        List<Person> activePersons = searchForPersons("Brukere", true);

        List<Person> inactivePersons = searchForPersons("Sluttede", false);
        List<Person> tempLeavePersons = searchForPersons("Permisjon", false);

        log.info("Number of users from LDAP: {}",
                activePersons.size() + inactivePersons.size() + tempLeavePersons.size());
        log.info("Number of active: {}", activePersons.size());
        log.info("Number of inactive: {}", inactivePersons.size());
        log.info("Number of people on temporary leave: {}", tempLeavePersons.size());

        List<Person> allPersons = personRepository.findAll();

        long inactivated = allPersons.stream()
                .filter(Person::isActive)
                .filter(person -> !activePersons.contains(person))
                .peek(person -> {
                    person.setActive(false);
                    personRepository.saveAndFlush(person);
                })
                .count();
        log.info("Made {} people inactive after syncing", inactivated);
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

        PersonMapper personMapper = new PersonMapper(personRepository, active, groupToRole);

        List<Person> persons = new ArrayList<>();

        do{
            List<Person> result = ldapTemplate.search(base, USER_FILTER, ctrl, personMapper, processor);
            persons.addAll(result);
            processor = new PagedResultsDirContextProcessor(PAGE_SIZE, processor.getCookie());
        }while (processor.getCookie().getCookie() != null);

        return persons;
    }

}
