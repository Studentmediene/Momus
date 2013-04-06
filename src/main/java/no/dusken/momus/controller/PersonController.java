package no.dusken.momus.controller;

import no.dusken.momus.model.Person;
import no.dusken.momus.model.Role;
import no.dusken.momus.service.PersonRepository;
import no.dusken.momus.service.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@Transactional
@RequestMapping("/person")
public class PersonController {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    RoleRepository roleRepository;

    @RequestMapping("/add")
    public @ResponseBody String addStuff() {
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("Role222"));

        roles = roleRepository.save(roles);

        Person person = new Person(roles, "Gunild", "B", "fggggggg@m.com", "445345355");


        personRepository.saveAndFlush(person);
        return "success";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody String addPerson(@RequestBody Person person) {
        return person.getEmail();
    }

    @RequestMapping("/get")
    public @ResponseBody Person getPerson() {
        Person person = personRepository.findOne(1L);
        person.getRoles().size();
//        person = personRepository.save(person);
        if(false) //testing
        throw new RuntimeException("hehe");
        return person;
    }
}
