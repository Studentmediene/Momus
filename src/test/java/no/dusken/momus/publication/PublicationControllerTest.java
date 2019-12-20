package no.dusken.momus.publication;

import no.dusken.momus.common.AbstractControllerTest;
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
    protected void internalSetup() {
        pub1 = publicationService.createPublication(
                Publication.builder().name("UD").releaseDate(LocalDate.now()).build(), 10);
        pub2 = publicationService.createPublication(
                Publication.builder().name("UD2").releaseDate(LocalDate.now().plusDays(10)).build(), 10);
    }

    @Test
    public void getAllPublications() throws Exception {
        performGetExpectOk("/api/publications").andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    public void getPublicationById() throws Exception {
        performGetExpectOk("/api/publications/" + pub1.getId()).andExpect(jsonPath("$.name", is("UD")));
    }

    @Test
    public void savePublication() throws Exception {
        performPostExpectOk(
                "/api/publications",
                TestUtil.toJsonString(Publication.builder().name("UD3").releaseDate(LocalDate.now().plusDays(20)).build()));

        assert publicationRepository.findAll().size() == 3;
    }

    @Test
    public void updatePublicationMetadata() throws Exception {
        Publication newPub1 = Publication.builder().name("UDUD").releaseDate(pub1.getReleaseDate()).build();
        newPub1.setId(pub1.getId());

        performPatchExpectOk("/api/publications/" + pub1.getId() + "/metadata", TestUtil.toJsonString(newPub1));

        newPub1 = publicationRepository.findById(pub1.getId()).get();

        assert newPub1.getName().equals("UDUD");
    }

    @Test
    public void getActive() throws Exception {
        performGetExpectOk("/api/publications/active").andExpect(jsonPath("$.id", is(pub1.getId().intValue())));
    }
}
