package no.dusken.momus.controller;

import no.dusken.momus.authentication.UserDetailsService;
import no.dusken.momus.model.Person;
import no.dusken.momus.model.Section;
import no.dusken.momus.service.repository.PersonRepository;
import no.dusken.momus.service.repository.SectionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
        performGetExpectOk("/api/person").andExpect(jsonPath("$.length()", is(0)));
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
        personRepository.save(users);
        personRepository.flush();
        performGetExpectOk("/api/person").andExpect(jsonPath("$.length()", is(users.size())));
    }

    @Test
    public void getNonexistentID() throws Exception {
        performGetExpectOk("/api/person/2").andExpect(content().string(""));
    }

    @Test
    public void meEqualsId() throws Exception {
        String me = performGetExpectOk("/api/person/me").andReturn().getResponse().getContentAsString();
        String id1 = performGetExpectOk("/api/person/1").andReturn().getResponse().getContentAsString();
        assert me.equals(id1);
    }

    @Test
    public void setFavouriteSection() throws Exception {
        mockMvc.perform(patch("/api/person/me/favouritesection")
                .accept(MediaType.APPLICATION_JSON)
                .param("section", "1"))
                .andDo(print()).andExpect(status().isNotFound());
        Section sport = Section.builder().name("Sport").build();
        sport = sectionRepository.saveAndFlush(sport);
        mockMvc.perform(patch("/api/person/me/favouritesection")
                .accept(MediaType.APPLICATION_JSON)
                .param("section", sport.getId().toString()))
                .andDo(print()).andExpect(status().isOk());

        assert userDetailsService.getLoggedInPerson().getFavouritesection().getId().equals(sport.getId());
    }
}
