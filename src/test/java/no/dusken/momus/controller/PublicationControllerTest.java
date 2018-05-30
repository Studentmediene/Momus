package no.dusken.momus.controller;

import no.dusken.momus.model.Publication;
import no.dusken.momus.service.PublicationService;
import no.dusken.momus.service.repository.PublicationRepository;
import no.dusken.momus.util.TestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


public class PublicationControllerTest extends AbstractControllerTest {

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private PublicationRepository publicationRepository;

    Publication pub1;
    Publication pub2;

    @Override
    void internalSetup() {
        pub1 = publicationService.savePublication(
                Publication.builder().name("UD").releaseDate(LocalDate.now()).build(), 10);
        pub2 = publicationService.savePublication(
                Publication.builder().name("UD2").releaseDate(LocalDate.now().plusDays(10)).build(), 10);
    }

    @Test
    public void getAllPublications() throws Exception {
        performGetExpectOk("/publications").andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    public void getPublicationById() throws Exception {
        performGetExpectOk("/publications/" + pub1.getId()).andExpect(jsonPath("$.name", is("UD")));
    }

    @Test
    public void savePublication() throws Exception {
        performPostExpectOk(
                "/publications",
                TestUtil.toJsonString(Publication.builder().name("UD3").releaseDate(LocalDate.now().plusDays(20)).build()));

        assert publicationRepository.findAll().size() == 3;
    }

    @Test
    public void updatePublication() throws Exception {
        Publication newPub1 = Publication.builder().name("UDUD").releaseDate(pub1.getReleaseDate()).build();
        newPub1.setId(pub1.getId());

        performPutExpectOk("/publications/" + pub1.getId(), TestUtil.toJsonString(newPub1));

        newPub1 = publicationRepository.findOne(pub1.getId());

        assert newPub1.getName().equals("UDUD");
    }

    @Test
    public void getActive() throws Exception {
        performGetExpectOk("/publications/active").andExpect(jsonPath("$.id", is(pub1.getId().intValue())));
    }
}
