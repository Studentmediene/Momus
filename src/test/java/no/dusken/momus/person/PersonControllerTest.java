package no.dusken.momus.person;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import no.dusken.momus.common.AbstractControllerTest;
import no.dusken.momus.person.authentication.UserDetailsService;
import no.dusken.momus.section.SectionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PersonControllerTest extends AbstractControllerTest {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    UserDetailsService userDetailsService;

    @Test
    public void getAllPersonsAfterAdd() throws Exception {
        performGetExpectOk("/api/people").andExpect(jsonPath("$.length()", is(0)));
        Person eirik = Person.builder()
                .id(1L)
                .guid(UUID.randomUUID())
                .username("eirik")
                .name("Eirik")
                .email("e@smint.no")
                .phoneNumber("0")
                .active(true)
                .build();
        Person eivind = Person.builder()
                .id(2L)
                .guid(UUID.randomUUID())
                .username("eivind")
                .name("Eivind")
                .email("ei@smint.no")
                .phoneNumber("0")
                .active(true)
                .build();
        List<Person> users = Arrays.asList(eirik, eivind);
        personRepository.saveAll(users);
        personRepository.flush();
        performGetExpectOk("/api/people").andExpect(jsonPath("$.length()", is(users.size())));
    }

    @Test
    public void getNonexistentID() throws Exception {
        mockMvc.perform(get("/api/people/2"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void meEqualsId() throws Exception {
        String me = performGetExpectOk("/api/people/me").andReturn().getResponse().getContentAsString();
        String id1 = performGetExpectOk("/api/people/1").andReturn().getResponse().getContentAsString();
        assert me.equals(id1);
    }
}
