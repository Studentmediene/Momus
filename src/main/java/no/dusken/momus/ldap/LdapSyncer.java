/*
 * Copyright 2014 Studentmediene i Trondheim AS
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
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.List;

@Service
public class LdapSyncer {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    LdapTemplate ldapTemplate;

    @Autowired
    PersonRepository personRepository;

    private boolean disabled = false;

    /**
     * This will be run when the server starts
     */
    @PostConstruct
    public void startUp() {
        sync();
    }

    /**
     * Will pull data from SmmDb and update our local copy
     * 02:00 each day (second minute hour day month weekdays)
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void sync() {
        if (disabled) {
            logger.info("Not syncing, it is disabled");
            return;
        }
        logger.info("Starting LDAP sync");

        long start = System.currentTimeMillis();

        List<Person> allPersons = getAllPersonsFromLdap();
        personRepository.save(allPersons);


        long end = System.currentTimeMillis();
        long timeUsed = end - start;
        logger.info("Done syncing from LDAP, it took {}ms", timeUsed);
    }

    private List<Person> getAllPersonsFromLdap() {
        final int[] activeCount = {0};

        List<Person> persons = ldapTemplate.search("ou=Users", "(objectClass=person)", new AttributesMapper<Person>() {
            @Override
            public Person mapFromAttributes(Attributes attributes) throws NamingException {
                Long id = Long.valueOf((String) attributes.get("uidNumber").get());
                String firstName = attributes.get("givenName") != null ? (String) attributes.get("givenName").get() : "";
                String fullName = attributes.get("cn") != null ? (String) attributes.get("cn").get() : "";
                String userName = attributes.get("uid") != null ? (String) attributes.get("uid").get() : "";
                String email = attributes.get("mail") != null ? (String) attributes.get("mail").get() : "";
                String telephoneNumber = attributes.get("telephoneNumber") != null ? (String) attributes.get("telephoneNumber").get() : "";
                boolean isActive = false;

                Attribute memberOf1 = attributes.get("memberOf");
                if (memberOf1 != null) {
                    NamingEnumeration<?> memberOf = memberOf1.getAll();
                    while (memberOf.hasMore()) {
                        String group = (String) memberOf.next();
                        if (group.equalsIgnoreCase("cn=Active,ou=Sections,ou=Org,ou=Groups,dc=studentmediene,dc=no")) {
                            isActive = true;
                            activeCount[0]++;
                            break;
                        }
                    }

                }


                return new Person(id, userName, firstName, fullName, email, telephoneNumber, isActive);
            }
        });
        logger.info("Number of users from LDAP: {}", persons.size());
        logger.info("Number of active: {}", activeCount[0]);

        return persons;
    }


    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
