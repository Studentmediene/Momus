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

import no.dusken.momus.authentication.AuthUserDetails;
import no.dusken.momus.authentication.Token;
import no.dusken.momus.authentication.UserAuthorities;
import no.dusken.momus.authentication.UserLoginService;
import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.Group;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.GroupRepository;
import no.dusken.momus.service.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
        Group userGroup = new Group("User");
        groupRepository.saveAndFlush(userGroup);

        Group adminGroup = new Group("Admin");
        groupRepository.saveAndFlush(adminGroup);

        Group photoGroup = new Group("Photographer");
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

    @RequestMapping("/logTest")
    public @ResponseBody void logTest() {
        logger.info("Yo, jeg logger info!");

        logger.warn("advarsel, waaaarn!");

        throw new RuntimeException("oooomgmmggm");
    }
}
