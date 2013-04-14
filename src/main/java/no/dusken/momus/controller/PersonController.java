package no.dusken.momus.controller;

import no.dusken.momus.model.Person;
import no.dusken.momus.model.Role;
import no.dusken.momus.service.PersonRepository;
import no.dusken.momus.service.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Transactional
@RequestMapping("/person")
public class PersonController {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    RoleRepository roleRepository;

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Person addPerson(@RequestBody Person person) {
        return personRepository.saveAndFlush(person);
    }

    @RequestMapping(value = "/addroles/{id}", method = RequestMethod.PUT)
    public @ResponseBody Person setRolesToPerson(@PathVariable("id") Long id, @RequestBody List<Role> roles) {
        Person person = personRepository.findOne(id);

        person.setRoles(roles);

        return personRepository.saveAndFlush(person);
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Person> getAllPersons() {
        return personRepository.findAll();
    }

}
