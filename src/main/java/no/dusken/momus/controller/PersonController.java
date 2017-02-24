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

import com.fasterxml.jackson.databind.util.JSONPObject;
import no.dusken.momus.authentication.SamlUserDetailsService;
import no.dusken.momus.authentication.UserLoginService;
import no.dusken.momus.model.LandingPage;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.LandingPageRepository;
import no.dusken.momus.service.repository.PersonRepository;
import no.dusken.momus.service.LandingPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Transactional
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private LandingPageRepository landingPageRepository;


    @Autowired
    private LandingPageService landingPageService;

    @Autowired
    private SamlUserDetailsService userDetailsService;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Person> getAllPersons() {
        return personRepository.findByActiveTrue();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody Person getPersonById(@PathVariable("id") Long id) {
        return personRepository.findOne(id);
    }


    @RequestMapping("/me")
    public @ResponseBody Person getCurrentUser() {
        return userDetailsService.getLoggedInPerson();
    }

    @RequestMapping(value="/landing", method = RequestMethod.GET)
    public @ResponseBody LandingPage getLandingPage() {
        return landingPageRepository.findByOwner_Id(userDetailsService.getLoggedInPerson().getId());
    }

    @RequestMapping(value="/landing/{landing}", method = RequestMethod.GET)
    public @ResponseBody LandingPage saveLandingPage(@PathVariable("landing") String landing){
        return landingPageService.saveLandingPage(landing);
    }
}
