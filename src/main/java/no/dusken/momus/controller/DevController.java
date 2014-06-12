/*
 * Copyright 2014 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.dusken.momus.controller;

import no.dusken.momus.authentication.AuthUserDetails;
import no.dusken.momus.authentication.Token;
import no.dusken.momus.authentication.UserAuthorities;
import no.dusken.momus.authentication.UserLoginService;
import no.dusken.momus.model.*;
import no.dusken.momus.service.repository.*;
import no.dusken.momus.smmdb.Syncer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dev only, not accessible when live
 * Utility methods etc. goes here.
 */
@Controller
@RequestMapping("/dev")
public class DevController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserAuthorities userAuthorities;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private DispositionRepository dispositionRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private ArticleTypeRepository articleTypeRepository;

    @Autowired
    private ArticleStatusRepository articleStatusRepository;

    @Autowired
    private Syncer syncer;

    /**
     * Bypass login and logs you in as the user with the provided id
     */
    @RequestMapping("/login/{id}")
    public @ResponseBody void login(@PathVariable("id") Long id) {
        AuthUserDetails user = userAuthorities.getAuthoritiesForUser(id);
        Token token = new Token(null, user);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    /**
     * Syncs stuff from SmmDB
     */
    @RequestMapping("/sync")
    public @ResponseBody String sync() {
        syncer.sync();
        return "sync ok";
    }

    @RequestMapping("/test")
    public @ResponseBody String test() {
        syncer.sync();
        return "ok";
    }

    @RequestMapping("/editor")
    @PreAuthorize("hasRole('momus:editor')")
    public @ResponseBody String editor() {
        return "editor ok";
    }

    @RequestMapping("/createArticles")
    public @ResponseBody String createTestArticle1() {
        Article article1 = new Article();

        Set<Person> journalists1 = new HashSet<>();
        Set<Person> photographers1 = new HashSet<>();
        journalists1.add(personRepository.findOne(594L));
        photographers1.add(personRepository.findOne(600L));

        article1.setJournalists(journalists1);
        article1.setPhotographers(photographers1);
        article1.setContent(" Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus consequat ultricies nibh nec gravida. Proin sed posuere quam. Interdum et malesuada fames ac ante ipsum primis in faucibus. Aenean a augue nec lectus aliquet euismod et eget diam. Morbi mattis ante eget neque tincidunt porttitor. Morbi in pellentesque ante, vitae tempus leo. Phasellus ut augue elit. Ut porta vulputate odio, quis vestibulum mi pellentesque sit amet. Nullam bibendum elit et mauris pretium molestie quis in elit. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Aliquam ac nibh vitae dolor tincidunt convallis sit amet ut tellus. Aenean lacinia rutrum ligula, eu pellentesque dolor viverra vel. ");
        article1.setNote("detta er et nottat");
        article1.setName("Artikkelnavn");

        // Setting article Type
        List<ArticleType> articleTypes = articleTypeRepository.findAll();
        for (ArticleType articleType : articleTypes) {
            if (articleType.getName().equals("KulturRaport")){
                article1.setType(articleType);
            }
        }

        // Setting article status
        List<ArticleStatus> articleStatuses = articleStatusRepository.findAll();
        for (ArticleStatus articlestatus : articleStatuses) {
            if (articlestatus.getName().equals("Skrives")){
                article1.setStatus(articlestatus);
            }
        }


        articleRepository.save(article1);

        Article article2 = new Article();

        Set<Person> journalists2 = new HashSet<>();
        Set<Person> photographers2 = new HashSet<>();
        journalists2.add(personRepository.findOne(601L));
        photographers2.add(personRepository.findOne(594L));

        article2.setJournalists(journalists2);
        article2.setPhotographers(photographers2);
        article2.setContent(" Integer id libero diam. Curabitur a pellentesque risus. Fusce ac justo id erat posuere dictum vel et arcu. Quisque et purus nibh. Fusce vel fringilla arcu. Pellentesque ac est mauris. Fusce quis tellus posuere, pharetra ante sit amet, elementum nibh. Donec pretium, lacus et porttitor placerat, diam quam posuere libero, sit amet dignissim tortor est nec justo. Donec in pulvinar risus. Donec at eleifend ligula, quis porta diam. Sed at rutrum est. Proin molestie euismod nunc, a blandit ligula egestas quis. Fusce et sollicitudin dui. Pellentesque vulputate eros id luctus porta. Proin quis urna sed sem lobortis sollicitudin. Donec non diam viverra, dictum arcu at, porta odio. ");
        article2.setNote("detta er også et nottat");
        article2.setName("Artikkelnavn 2: Electric Boogaloo");

        List<ArticleType> articleTypes2 = articleTypeRepository.findAll();
        for (ArticleType articleType2 : articleTypes2) {
            if (articleType2.getName().equals("KulturRaport")){
                article2.setType(articleType2);
            }
        }

        // Setting article status
        for (ArticleStatus articlestatus : articleStatuses) {
            if (articlestatus.getName().equals("Desk")){
                article2.setStatus(articlestatus);
            }
        }


        articleRepository.save(article2);

        return "ok";
    }

    @RequestMapping("/createDisp")
    public @ResponseBody String createTestDisp() {

        Disposition disp1 =  dispositionRepository.save(new Disposition(1L));
        Set<Page> pageSet = new HashSet<>();
        Page dummyPage1 = pageRepository.save(new Page());
        dummyPage1.setNote("Page_Disp");
        dummyPage1.setSection(sectionRepository.findOne(1L));
        dummyPage1 = pageRepository.save(dummyPage1);
        pageSet.add(dummyPage1);
        disp1.setPages(pageSet);

        Disposition save = dispositionRepository.save(disp1);
        return "ok";
    }

    @RequestMapping("/createSections")
    public @ResponseBody String createTestSections() {
        sectionRepository.save(new Section("FORSIDE"));
        sectionRepository.save(new Section("INNHOLD"));
        sectionRepository.save(new Section("ANNONSE"));
        sectionRepository.save(new Section("NYHET"));
        sectionRepository.save(new Section("TRANSIT"));
        sectionRepository.save(new Section("FORSKNINGSFUNN"));
        sectionRepository.save(new Section("DAGSORDEN"));
        sectionRepository.save(new Section("MENINGER"));
        sectionRepository.save(new Section("AKTUALITET"));
        sectionRepository.save(new Section("SMÅREP"));
        sectionRepository.save(new Section("KULTUR"));
        sectionRepository.save(new Section("SPIT"));
        sectionRepository.save(new Section("BAKSIDE"));
        sectionRepository.save(new Section("TEST"));


        articleTypeRepository.save(new ArticleType("KulturRaport"));

        return "ok";
    }

    @RequestMapping("/createArticleStatus")
    public @ResponseBody String createTestStatuses() {

        articleStatusRepository.save(new ArticleStatus("Desk"));
        articleStatusRepository.save(new ArticleStatus("Skrives"));
        articleStatusRepository.save(new ArticleStatus("Test"));

        return "ok";
    }
}
