package no.dusken.momus.common;

import no.dusken.momus.common.config.TestConfig;
import no.dusken.momus.person.Person;
import no.dusken.momus.person.PersonRepository;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@WebAppConfiguration
@Transactional
public abstract class AbstractControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    PersonRepository personRepository;

    protected MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        Person me = Person.builder().id(1L).build();
        personRepository.saveAndFlush(me);
        internalSetup();
    }

    protected void internalSetup() {
        // Intentionally empty, override if necessary
    }

    public RequestBuilder buildGet(String url) {
        return get(url).accept(MediaType.APPLICATION_JSON);
    }

    public RequestBuilder buildPut(String url, String content) {
        return put(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
    }

    public RequestBuilder buildPost(String url, String content) {
        return post(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
    }

    public RequestBuilder buildPatch(String url, String content) {
        return patch(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
    }

    public RequestBuilder buildDelete(String url) {
        return delete(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
    }

    public ResultActions performPutExpectOk(String url, String content) throws Exception {
        return mockMvc.perform(buildPut(url, content))
                .andDo(print())
                .andExpect(status().isOk());
    }

    public ResultActions performGetExpectOk(String url) throws Exception {
        return mockMvc.perform(buildGet(url))
                .andDo(print())
                .andExpect(status().isOk());
    }

    public ResultActions performPatchExpectOk(String url, String content) throws Exception {
        return mockMvc.perform(buildPatch(url, content))
                .andDo(print())
                .andExpect(status().isOk());
    }

    public ResultActions performPostExpectOk(String url, String content) throws Exception {
        return mockMvc.perform(buildPost(url, content))
                .andDo(print())
                .andExpect(status().isOk());
    }

    public ResultActions performDeleteExpectOk(String url) throws Exception {
        return mockMvc.perform(buildDelete(url))
                .andDo(print())
                .andExpect(status().isOk());
    }


}
