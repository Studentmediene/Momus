package no.dusken.momus.controller;

import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.PersonRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class PersonControllerTest extends AbstractControllerTest {

    @Autowired
    PersonRepository personRepository;

    @Test
    public void getAllPersonsAfterAdd() throws Exception {
        performGetExpectOk("/person").andExpect(jsonPath("$.length()", is(0)));
        Person eirik = new Person(1L, "eirik", "Eirik", "S", "e@smint.no", "0", true);
        Person eivind = new Person(2L, "eivind", "Eivind", "G", "ei@smint.no", "0", true);
        List<Person> users = Arrays.asList(eirik, eivind);
        personRepository.save(users);
        personRepository.flush();
        performGetExpectOk("/person").andExpect(jsonPath("$.length()", is(users.size())));
    }
}
