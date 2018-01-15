package no.dusken.momus.controller;

import no.dusken.momus.model.FavouriteSection;
import no.dusken.momus.model.Section;
import no.dusken.momus.service.repository.SectionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static no.dusken.momus.util.TestUtil.toJsonString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.is;

public class FavouriteSectionControllerTest extends AbstractControllerTest {

    @Autowired
    SectionRepository sectionRepository;

    @Test
    public void getEmptyFavSection() throws Exception {
        performGetExpectOk("/favsection")
                .andExpect(content().string(""));
    }

    @Test
    public void setAndGetFavSection() throws Exception {
        FavouriteSection favSection = new FavouriteSection();
        Section itSection = new Section("IT");
        sectionRepository.saveAndFlush(itSection);
        favSection.setSection(itSection);
        String favSectionString = toJsonString(favSection);

        performPutExpectOk("/favsection", favSectionString);
        performGetExpectOk("/favsection")
                .andExpect(jsonPath("$.owner.id", is(1)))
                .andExpect(jsonPath("$.section.name", is("IT")));
    }

    @Test
    public void setAndEditFavSection() throws Exception {
        FavouriteSection favSection1 = new FavouriteSection();
        Section itSection = new Section("IT");
        sectionRepository.saveAndFlush(itSection);
        favSection1.setSection(itSection);
        String favSection1String = toJsonString(favSection1);

        FavouriteSection favSection2 = new FavouriteSection();
        Section sportSection = new Section("Sport");
        sectionRepository.saveAndFlush(sportSection);
        favSection2.setSection(sportSection);
        String favSection2String = toJsonString(favSection2);

        performPutExpectOk("/favsection", favSection1String);
        performGetExpectOk("/favsection")
                .andExpect(jsonPath("$.owner.id", is(1)))
                .andExpect(jsonPath("$.section.name", is("IT")));

        performPutExpectOk("/favsection", favSection2String);
        performGetExpectOk("/favsection")
                .andExpect(jsonPath("$.owner.id", is(1)))
                .andExpect(jsonPath("$.section.name", is("Sport")));
    }


}
