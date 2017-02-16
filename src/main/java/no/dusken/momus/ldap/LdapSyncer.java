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
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultTlsDirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapContext;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class LdapSyncer {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final int PAGE_SIZE = 1000;

    private Long lastId;

    @Autowired
    LdapTemplate ldapTemplate;

    @Autowired
    LdapContextSource contextSource;

    @Autowired
    PersonRepository personRepository;


    @Value("${ldap.syncEnabled}")
    private boolean enabled;

    /**
     * This will be run when the server starts
     */
    @PostConstruct
    public void startUp() {
        DefaultTlsDirContextAuthenticationStrategy tls = new DefaultTlsDirContextAuthenticationStrategy();
        tls.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        contextSource.setAuthenticationStrategy(tls);

        sync();
    }

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

    /**
     * Syncs the person database with LDAP
     */
    private void syncAllPersonsFromLdap() {

        lastId = 0L; // Contains the last granted id, for faster look-up for next free id

        // Get all active persons, with base ou=Brukere
        List<Person> activePersons = searchForPersons("ou=Brukere", "(objectClass=person)", PAGE_SIZE, true);

        //Get all inactive persons, with base ou=DeepDarkVoidOfHell. funny huh
        //List<Person> inactivePersons = searchForPersons("ou=DeepDarkVoidOfHell", "(objectClass=person)", PAGE_SIZE, false);

        logger.info("Number of users from LDAP: {}", activePersons.size());
        logger.info("Number of active: {}", activePersons.size());
    }

    /**
     *
     * @param base The base for the ldap search
     * @param filter Ldap search filter
     * @param pageSize The page size of each search. Must use since ad only allows 1000 results at a time
     * @param active Whether or not the user should be flagged as active
     * @return A list of the found persons
     */
    private List<Person> searchForPersons(String base, String filter, int pageSize, final boolean active){
        // For pagination
        PagedResultsDirContextProcessor processor = new PagedResultsDirContextProcessor(pageSize, null);

        // For searching through subgroups
        SearchControls ctrl = new SearchControls();
        ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);

        List<Person> persons = new ArrayList<>();

        do{
            List<Person> result = ldapTemplate.search(base, filter, ctrl, new AttributesMapper<Person>() {
                @Override
                public Person mapFromAttributes(Attributes attributes) throws NamingException {
                    Person person = LdapSyncer.this.mapFromAttributes(attributes, active);
                    personRepository.saveAndFlush(person); //Must save to db per person because of how we give ids
                    return person;
                }
            }, processor);
            persons.addAll(result);
            processor = new PagedResultsDirContextProcessor(pageSize, processor.getCookie());
        }while (processor.getCookie().getCookie() != null);

        return persons;
    }

    private Person mapFromAttributes(Attributes attributes, boolean active) throws NamingException {
        String firstName = attributes.get("givenName") != null ? (String) attributes.get("givenName").get() : "";
        String lastName = attributes.get("sn") != null ? (String) attributes.get("sn").get() : "";
        String fullName = firstName + " " + lastName;
        String userName = attributes.get("sAMAccountName") != null ? (String) attributes.get("sAMAccountName").get() : "";
        String email = attributes.get("mail") != null ? (String) attributes.get("mail").get() : "";
        String telephoneNumber = attributes.get("telephoneNumber") != null ? (String) attributes.get("telephoneNumber").get() : "";

        Long id = attributes.get("uidNumber") != null ? Long.valueOf((String) attributes.get("uidNumber").get()) : -1L;

        id = findId(id, userName);

        return new Person(id, userName, firstName, fullName, email, telephoneNumber, active);
    }

    /**
     * This method allocates an ID to the given username if it hasn't one already
     * @param id The id from ldap (should be -1 if none was found)
     * @param userName The username from ldap
     * @return The id granted to the username
     */
    private Long findId(Long id, String userName){

        // Use id from ldap if one was found
        if(id != -1){
            return id;
        }

        Person person = personRepository.findByUsername(userName);
        if(person == null){ // New person
            id = lastId;
            while(personRepository.findOne(id) != null){
                id++;
            }
            lastId = id;

            return id;
        }

        return person.getId();
    }

}
