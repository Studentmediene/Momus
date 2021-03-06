/*
 * Copyright 2016 Studentmediene i Trondheim AS
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.dusken.momus.config.MockToken;
import no.dusken.momus.ldap.LdapSyncer;
import no.dusken.momus.model.Advert;
import no.dusken.momus.model.Article;
import no.dusken.momus.model.ArticleReview;
import no.dusken.momus.model.ArticleStatus;
import no.dusken.momus.model.ArticleType;
import no.dusken.momus.model.Person;
import no.dusken.momus.model.Publication;
import no.dusken.momus.model.Section;
import no.dusken.momus.service.PublicationService;
import no.dusken.momus.service.repository.AdvertRepository;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.ArticleReviewRepository;
import no.dusken.momus.service.repository.ArticleStatusRepository;
import no.dusken.momus.service.repository.ArticleTypeRepository;
import no.dusken.momus.service.repository.PersonRepository;
import no.dusken.momus.service.repository.SectionRepository;

/**
 * Dev only, not accessible when live
 * Utility methods etc. goes here.
 */
@RestController
@RequestMapping("/api/dev")
public class DevController {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ArticleTypeRepository articleTypeRepository;

    @Autowired
    private ArticleStatusRepository articleStatusRepository;

    @Autowired
    private ArticleReviewRepository articleReviewRepository;

    @Autowired
    private AdvertRepository advertRepository;

    @Autowired
    private Environment env;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    LdapSyncer ldapSyncer;

    @GetMapping("/ldaptest")
    public String ldaptest() {
        ldapSyncer.sync();
        return "oook";
    }

    @PostMapping("/generatedata")
    public String generatePublicationsAndArticles() {
        Random random = new Random();

        List<ArticleStatus> articleStatuses = articleStatusRepository.findAll();
        List<ArticleType> articleTypes = articleTypeRepository.findAll();
        List<Section> sections = sectionRepository.findAll();
        List<ArticleReview> articleReviews = articleReviewRepository.findAll();
        List<Person> people = new ArrayList<>(personRepository.findByActiveTrue());

        Publication p1 = new Publication();
        p1.setName("UD #1");
        p1.setReleaseDate(LocalDate.now().plusDays(7));
        p1 = publicationService.createPublication(p1, 50);

        Publication p2 = new Publication();
        p2.setName("UD #2");
        p2.setReleaseDate(LocalDate.now().plusDays(14));
        p2 = publicationService.createPublication(p2, 50);

        Article a1 = new Article();
        a1.setName("Lorem Ipsum");
        a1.setStatus(articleStatuses.get(random.nextInt(articleStatuses.size())));
        a1.setType(articleTypes.get(random.nextInt(articleTypes.size())));
        a1.setSection(sections.get(random.nextInt(sections.size())));
        a1.setReview(articleReviews.get(random.nextInt(articleReviews.size())));
        a1.setJournalists(new HashSet<>(Arrays.asList(people.get(random.nextInt(people.size())))));
        a1.setPhotographers(new HashSet<>(Arrays.asList(people.get(random.nextInt(people.size())))));
        a1.setGraphics(new HashSet<>(Arrays.asList(people.get(random.nextInt(people.size())))));
        a1.setGoogleDriveId("19MklrX5aiYHtdpSuiz_fNr7BfalpJH11O-Q6RdONSa0");
        a1.setNote("<p>Notat her</p>");
        a1.setPublication(p1);
        articleRepository.save(a1);

        Article a2 = new Article();
        a2.setName("Bacon Ipsum");
        a2.setStatus(articleStatuses.get(random.nextInt(articleStatuses.size())));
        a2.setType(articleTypes.get(random.nextInt(articleTypes.size())));
        a2.setSection(sections.get(random.nextInt(sections.size())));
        a2.setReview(articleReviews.get(random.nextInt(articleReviews.size())));
        a2.setJournalists(new HashSet<>(
                Arrays.asList(
                        people.get(random.nextInt(people.size())),
                        people.get(random.nextInt(people.size()))
                ))
        );
        a2.setPhotographers(new HashSet<>(Arrays.asList(people.get(random.nextInt(people.size())))));
        a2.setGraphics(new HashSet<>(Arrays.asList(people.get(random.nextInt(people.size())))));
        a2.setGoogleDriveId("1i8Bq6UYswttJYjAkEf5yRvZV-zPlzTSzW835wUq8s5s");
        a2.setNote("<p>Notat her</p>");
        a2.setPublication(p1);
        articleRepository.save(a2);

        Article a3 = new Article();
        a3.setName("Spit");
        a3.setStatus(articleStatuses.get(random.nextInt(articleStatuses.size())));
        a3.setType(articleTypes.get(random.nextInt(articleTypes.size())));
        a3.setSection(sectionRepository.findByName("Spit"));
        a3.setReview(articleReviews.get(random.nextInt(articleReviews.size())));
        a3.setJournalists(new HashSet<>(Arrays.asList(people.get(random.nextInt(people.size())))));
        a3.setGraphics(new HashSet<>(Arrays.asList(people.get(random.nextInt(people.size())))));
        a3.setUseIllustration(true);
        a3.setGoogleDriveId("1pVVqfVaAUeyyia3X3JYwrv9HXgg-9Flud_GURBsoShg");
        a3.setNote("<p>Notat her</p>");
        a3.setPublication(p1);
        articleRepository.save(a3);

        Article a5 = new Article();
        a5.setName("Tinder har dødd");
        a5.setStatus(articleStatuses.get(0));
        a5.setType(articleTypes.get(random.nextInt(articleTypes.size())));
        a5.setSection(sections.get(random.nextInt(sections.size())));
        a5.setReview(articleReviews.get(random.nextInt(articleReviews.size())));
        a5.setJournalists(new HashSet<>(Arrays.asList(people.get(random.nextInt(people.size())))));
        a5.setPhotographers(new HashSet<>(Arrays.asList(people.get(random.nextInt(people.size())))));
        a5.setGraphics(new HashSet<>(Arrays.asList(people.get(random.nextInt(people.size())))));
        a5.setGoogleDriveId("1DPb-IoPNbvkookwOupfT1oaPydrRG8FPUGs97eRUFWc");
        a5.setNote("<p>Notat her</p>");
        a5.setPublication(p2);
        articleRepository.save(a5);

        Advert ad1 = new Advert();
        ad1.setName("iBok");
        advertRepository.save(ad1);
        return "dummy articles and publications generated";
    }

    @GetMapping("/devmode")
    public boolean isDevmode() {
        return env.acceptsProfiles(Profiles.of("dev"));
    }

    @GetMapping("/noauth")
    public boolean isNoAuth() {
        return env.acceptsProfiles(Profiles.of("noAuth"));
    }

    @PostMapping("/login")
    public void login(@RequestBody String user) {
        Authentication token = authenticationManager.authenticate(new MockToken(user));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @PostMapping("/logout")
    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @GetMapping("/loginstate")
    public boolean isLoggedIn() {
        return !SecurityContextHolder.getContext().getAuthentication().getClass().equals(AnonymousAuthenticationToken.class);
    }
}
