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

        List<Person> allPersons = getAllPersonsFromLdap();
        personRepository.save(allPersons);
        personRepository.flush();


        long end = System.currentTimeMillis();
        long timeUsed = end - start;
        logger.info("Done syncing from LDAP, it took {}ms", timeUsed);
    }

    private List<Person> getAllPersonsFromLdap() {
        int pageSize = 1000;

        // Get all active persons, with base ou=Brukere
        List<Person> activePersons = searchForPersons("ou=Brukere", "(objectClass=person)", pageSize);

        //Get all inactive persons, with base ou=DeepDarkVoidOfHell. funny huh
        List<Person> inactivePersons = searchForPersons("ou=DeepDarkVoidOfHell", "(objectClass=person)", pageSize);

        logger.info("Number of users from LDAP: {}", activePersons.size()+inactivePersons.size());
        logger.info("Number of active: {}", activePersons.size());

        activePersons.addAll(inactivePersons);

        logger.info("Number adding: {}", activePersons.size());

        return activePersons;
    }

    private List<Person> searchForPersons(String base, String filter, int pageSize){
        PagedResultsDirContextProcessor processor = new PagedResultsDirContextProcessor(pageSize, null);
        List<Person> persons = new ArrayList<>();
        do{
            List<Person> result = ldapTemplate.search(base, filter, null, new AttributesMapper<Person>() {
                @Override
                public Person mapFromAttributes(Attributes attributes) throws NamingException {
                    return LdapSyncer.this.mapFromAttributes(attributes);
                }
            }, processor);
            persons.addAll(result);
            processor = new PagedResultsDirContextProcessor(pageSize, processor.getCookie());
        }while (processor.getCookie().getCookie() != null);

        return persons;
    }

    private Person mapFromAttributes(Attributes attributes) throws NamingException {
        String firstName = attributes.get("givenName") != null ? (String) attributes.get("givenName").get() : "";
        String lastName = attributes.get("sn") != null ? (String) attributes.get("sn").get() : "";
        String fullName = firstName + " " + lastName;
        String userName = attributes.get("sAMAccountName") != null ? (String) attributes.get("sAMAccountName").get() : "";
        String email = attributes.get("mail") != null ? (String) attributes.get("mail").get() : "";
        String telephoneNumber = attributes.get("telephoneNumber") != null ? (String) attributes.get("telephoneNumber").get() : "";

        Long id = findId(userName);

        System.out.println(userName);

        return new Person(id, userName, firstName, fullName, email, telephoneNumber, true);
    }

    /**
     * This method allocates a ID to the given username if it hasn't one already
     * @param userName The username gotten from ldap
     * @return The id granted to the username
     */
    private Long findId(String userName){
        Person person = personRepository.findByUsername(userName);
        if(person == null){ // New person
            Person other;
            Long id = personRepository.findFirstByOrderByIdDesc().get(0).getId();
            System.out.println(id);
            do {
                id++;
                other = personRepository.findOne(id);
            }while (other != null);
            logger.info("Found id for {}: {}", userName, id);

            return id;
        }

        return person.getId();
    }

}
