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

package no.dusken.momus.person.ldap;

import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.person.*;
import no.dusken.momus.person.ldap.PersonMapper.Status;

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
@Slf4j
public class LdapSyncer {

    private final int PAGE_SIZE = 1000;

    private final String USER_FILTER = "(&(objectClass=user)(!(objectClass=inetOrgPerson)))";

    private final LdapTemplate ldapTemplate;

    private final PersonMapper personMapper;

    public LdapSyncer(LdapTemplate ldapTemplate, PersonMapper personMapper) {
        this.ldapTemplate = ldapTemplate;
        this.personMapper = personMapper;
    }

    @Value("${ldap.syncEnabled}")
    private boolean enabled;

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
        sync();
    }

    public void syncAllPersonsFromLdap() {
        List<Person> activePersons = searchForPersons(Status.ACTIVE.getBase(), true);

        List<Person> inactivePersons = searchForPersons(Status.INACTIVE.getBase(), false);
        List<Person> tempLeavePersons = searchForPersons(Status.TEMP_LEAVE.getBase(), false);

        log.info("Number of users from LDAP: {}",
                activePersons.size() + inactivePersons.size() + tempLeavePersons.size());
        log.info("Number of active: {}", activePersons.size());
        log.info("Number of inactive: {}", inactivePersons.size());
        log.info("Number of people on temporary leave: {}", tempLeavePersons.size());
    }

    /**
     * @param ou group to search in
     * @param active Whether or not the user should be flagged as active
     * @return A list of the found persons
     */
    private List<Person> searchForPersons(String base, final boolean active){
        // For pagination
        PagedResultsDirContextProcessor processor = new PagedResultsDirContextProcessor(PAGE_SIZE, null);

        // For searching through subgroups
        SearchControls ctrl = new SearchControls();
        ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);

        List<Person> persons = new ArrayList<>();

        do{
            List<Person> result = ldapTemplate.search(base, USER_FILTER, ctrl, personMapper.createMapper(active), processor);
            persons.addAll(result);
            processor = new PagedResultsDirContextProcessor(PAGE_SIZE, processor.getCookie());
        }while (processor.getCookie().getCookie() != null);

        return persons;
    }
}
