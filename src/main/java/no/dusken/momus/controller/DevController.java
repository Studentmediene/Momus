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

package no.dusken.momus.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import no.dusken.momus.authentication.AuthUserDetails;
import no.dusken.momus.authentication.Token;
import no.dusken.momus.authentication.UserAuthorities;
import no.dusken.momus.model.Group;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.GroupRepository;
import no.dusken.momus.service.repository.PersonRepository;
import no.dusken.momus.smmdb.SmmdbConnector;
import no.dusken.momus.smmdb.Syncer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Dev only, not accessible when live
 * Utility methods etc. goes here.
 */
@Controller
@RequestMapping("/dev")
public class DevController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserAuthorities userAuthorities;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    Syncer syncer;

    /**
     * Logs in without token or anything
     */
    @RequestMapping("/login/{id}")
    public @ResponseBody void login(@PathVariable("id") Long id) {
        AuthUserDetails user = userAuthorities.getAuthoritiesForUser(id);
        Token token = new Token(null, user);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    /**
     * Todo: Either have a copy of live data, or just some other dump to import to db
     * Creates some entities used for testing
     */
    @RequestMapping("/create")
    public @ResponseBody void createData() {
        System.out.println("Create users");
        Group userGroup = new Group(1L, "User");
        groupRepository.saveAndFlush(userGroup);

        Group adminGroup = new Group(2L, "Admin");
        groupRepository.saveAndFlush(adminGroup);

        Group photoGroup = new Group(3L, "Photographer");
        groupRepository.saveAndFlush(photoGroup);

        Person admin = new Person(new HashSet<Group>(Arrays.asList(new Group[]{userGroup, adminGroup})), "Mats", "Svensson", "mats@matsemann.com", "47385324");
        personRepository.saveAndFlush(admin);

        Person photographer = new Person(new HashSet<Group>(Arrays.asList(new Group[]{userGroup, photoGroup})), "Sven", "Fotosvensson", "sven@foto.com", "111111");
        personRepository.saveAndFlush(photographer);
    }

    @RequestMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody String adminTest() {
        return "admin ok!!";
    }

    @RequestMapping("/photographer")
    @PreAuthorize("hasRole('ROLE_PHOTOGRAPHER')")
    public @ResponseBody String photoTest() {
        return "photo ok!!";
    }

    @RequestMapping("/test")
    public @ResponseBody String test() {
        syncer.sync();
        return "wey";
    }

    @RequestMapping("/logTest")
    public @ResponseBody void logTest() {
        logger.info("Yo, jeg logger info!");

        logger.warn("advarsel, waaaarn!");

        throw new RuntimeException("oooomgmmggm");
    }

    @RequestMapping("smmdbUser")
    public @ResponseBody String smmdbUser() {
//        return smmdbConnector.getAllUsers();
        return "";
    }

    @RequestMapping("/json")
    public @ResponseBody void json() throws IOException {
        String json = "{\"username\": \"mats\", \"phone_number\": null, \"last_name\": \"Svensson\", \"last_updated\": \"2013-09-15T12:51:38.139717\", \"about\": null, \"groups\": [{\"id\": 2, \"name\": \"admin\"}], \"active\": true, \"id\": 594, \"private_email\": null, \"first_name\": \"Mats\", \"created\": \"2013-09-15T12:51:38.139661\", \"birthdate\": null, \"email\": null}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Person person = mapper.readValue(new JsonFactory().createParser(json), Person.class);

        ObjectReader reader = mapper.reader(Person.class);


        logger.debug(person.getUsername());
        logger.debug(person.getFirstName());
        logger.debug(person.getEmail());
    }
}
