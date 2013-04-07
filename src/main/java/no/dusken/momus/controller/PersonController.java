package no.dusken.momus.controller;

import no.dusken.momus.exceptions.RestException;
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
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

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

        Person person = new Person(roles, "Mats", "B", "fggggggg@m.com", "445345355");


        personRepository.saveAndFlush(person);
        return "success";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody String addPerson(@RequestBody Person person) {
        return person.getEmail();
    }

    @RequestMapping("/get")
    public @ResponseBody Person getPerson(HttpServletResponse response) {
        response.setHeader("Last-Modified", "Sat, 06 Apr 2013 12:45:26 GMT");
        response.setHeader("Cache-Control", "max-age=\"600\"");
        response.setHeader("Mats", "lol");
        Person person = personRepository.findOne(1L);
        person.getRoles().size();
//        person = personRepository.save(person);
        if(false) //testing
        throw new RuntimeException("hehe");
        return person;
    }

    @RequestMapping("/getAll")
    public @ResponseBody List<Person> getPersons(HttpServletResponse response, WebRequest webRequest) {
        Date date = new SimpleDateFormat("dd.MM.yyyy").parse("06.04.2013", new ParsePosition(0));
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);

        if(webRequest.checkNotModified(date.getTime())) {
            System.out.println("not modified");
            return null;
        }

        response.setHeader("Last-Modified", format.format(date) + " GMT");
        response.setHeader("Expires", "Mon, 08 Apr 2013 12:45:26 GMT");
        response.setHeader("Cache-Control", "max-age=\"600\"");
        response.setHeader("Mats", "lol");
        return personRepository.findAll();
    }
}
