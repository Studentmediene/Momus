package no.dusken.momus.controller;

import no.dusken.momus.model.Advert;
import no.dusken.momus.service.AdvertService;
import no.dusken.momus.service.repository.AdvertRepository;
import no.dusken.momus.util.TestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class AdvertControllerTest extends AbstractControllerTest {

    @Autowired
    private AdvertService advertService;

    @Autowired
    private AdvertRepository advertRepository;

    private Advert ad1;
    private Advert ad2;
    @Override
    void internalSetup() {
        super.internalSetup();

        ad1 = advertService.saveAdvert(Advert.builder().name("iBok").comment("ya").build());
        ad2 = advertService.saveAdvert(Advert.builder().name("barteguiden").comment("nope").build());
    }

    @Test
    public void getAllAdverts() throws Exception {
        performGetExpectOk("/api/advert").andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    public void getAdvertByID() throws Exception {
        performGetExpectOk("/api/advert/" + ad1.getId()).andExpect(jsonPath("$.id", is(ad1.getId().intValue())));
    }

    @Test
    public void saveAdvert() throws Exception {
        performPostExpectOk("/api/advert",
                TestUtil.toJsonString(Advert.builder().name("test").comment("yay").build()));

        assert advertRepository.findAll().size() == 3;
    }

    @Test
    public void updateComment() throws Exception {
        performPatchExpectOk("/api/advert/" + ad1.getId() + "/comment", TestUtil.toJsonString("ny kommentar"));

        assertEquals("ny kommentar", advertService.getAdvertById(ad1.getId()).getComment());
    }
}
