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

package no.dusken.momus.controller;

import no.dusken.momus.authentication.UserDetailsService;
import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.PersonService;
import no.dusken.momus.service.repository.PersonRepository;
import no.dusken.momus.service.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@Transactional
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private PersonService personService;

    /**
     * Gets all active persons. In addition, if article ids are supplied, will return all contributors on those
     * even if they are inactive
     */
    @GetMapping
    public @ResponseBody Set<Person> getActivePersons(@RequestParam(
            value="articleIds", required = false, defaultValue = "") List<Long> articleIds) {
        return personService.getActivePersonsAndArticleContributors(articleIds);
    }

    @GetMapping(value = "/{id}")
    public @ResponseBody Person getPersonById(@PathVariable("id") Long id) {
        return personRepository.findOne(id);
    }

    @GetMapping("/me")
    public @ResponseBody Person getCurrentUser() {
        return userDetailsService.getLoggedInPerson();
    }

    @PatchMapping("/me/favouritesection")
    public @ResponseBody Person updateFavouritesection(@RequestParam Long section) {
        if(!sectionRepository.exists(section)) {
            throw new RestException(
                    "Can't set favourite section, section with id " + section + " does not exist",
                    HttpServletResponse.SC_NOT_FOUND);
        }
        return personService.updateFavouritesection(
                userDetailsService.getLoggedInPerson(),
                sectionRepository.findOne(section));
    }

}