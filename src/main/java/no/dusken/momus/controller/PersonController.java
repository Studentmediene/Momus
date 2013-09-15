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

import no.dusken.momus.model.Group;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.GroupRepository;
import no.dusken.momus.service.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@Transactional
@RequestMapping("/person")
public class PersonController {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    GroupRepository groupRepository;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody Person getPersonById(@PathVariable("id") Long id) {
        return personRepository.findOne(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Person addPerson(@RequestBody Person person) {
        return personRepository.saveAndFlush(person);
    }
    @RequestMapping("/debugAdd")
    public @ResponseBody Person createNew() {
        Person person = new Person();
        person.setEmail("mats@matsemann.com");
        person.setFirstName("Mats");
        person.setLastName("Svensson");
        person.setPhoneNumber("47 38 53 24");

//        person.setEmail("test@testesen");
//        person.setFirstName("Test");
//        person.setLastName("Testson");
//        person.setPhone("815 493 00");

        return personRepository.saveAndFlush(person);
    }

    @RequestMapping(value = "/addroles/{id}", method = RequestMethod.PUT)
    public @ResponseBody Person setRolesToPerson(@PathVariable("id") Long id, @RequestBody Set<Group> groups) {
        Person person = personRepository.findOne(id);

        person.setGroups(groups);

        return personRepository.saveAndFlush(person);
    }

}
