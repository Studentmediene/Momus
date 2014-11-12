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

package no.dusken.momus.service;

import no.dusken.momus.model.*;
import no.dusken.momus.service.repository.*;
import no.dusken.momus.service.search.ArticleSearchParams;
import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Transactional
public class ArticleServiceTest extends AbstractTestRunner {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    PublicationRepository publicationRepository;

    @Autowired
    ArticleStatusRepository articleStatusRepository;

    @Autowired
    ArticleTypeRepository articleTypeRepository;

    @Autowired
    ArticleRevisionRepository articleRevisionRepository;

    @Autowired
    ArticleService articleService;

    private Article article1;
    private Article article2;
    private Article article3;
    private Article article4;


    @Before
    public void setUp() throws Exception {
        Person person1 = new Person(1L, "mts", "Mats", "Matsessen", "", "", true);
        Person person2 = new Person(2L, "aaa", "Kåre", "Kåressen", "", "", true);
        Person person3 = new Person(3L, "bbb", "Flaks", "Flaksesen", "", "", true);

        person1 = personRepository.save(person1);
        person2 = personRepository.save(person2);
        person3 = personRepository.save(person3);

        Publication publication1 = new Publication(1L);
        publication1.setName("Pub1");
        Publication publication2 = new Publication(2L);
        publication2.setName("Pub2");

        publication1 = publicationRepository.save(publication1);
        publication2 = publicationRepository.save(publication2);

        ArticleStatus articleStatus1 = new ArticleStatus();
        articleStatus1.setName("Skrives");
        articleStatusRepository.save(articleStatus1);

        // TODO: add section as well

        article1 = new Article();
        article1.setName("Artikkel 1");
        article1.setContent("Testinnhold for artikkel 1 yay");
        Set<Person> article1journalists = new HashSet<>();
        article1journalists.add(person1);
        article1journalists.add(person2);
        article1.setJournalists(article1journalists);
        article1.setPublication(publication1);
        article1 = articleRepository.saveAndFlush(article1);



        article2 = new Article();
        article2.setName("Artikkel 2");
        article2.setContent("Masse kult innhold, kan du søke i dette kanskje??");
        Set<Person> article2journalists = new HashSet<>();
        Set<Person> article2photographers = new HashSet<>();
        article2journalists.add(person2);
        article2photographers.add(person3);
        article2.setJournalists(article2journalists);
        article2.setPhotographers(article2photographers);
        article2.setPublication(publication1);
        article2 = articleRepository.save(article2);



        article3 = new Article();
        article3.setName("Artikkel 3");
        article3.setContent("Hei på deg, flott du leser testene! :)");
        Set<Person> article3journalists = new HashSet<>();
        Set<Person> article3photographers = new HashSet<>();
        article3journalists.add(person1);
        article3photographers.add(person2);
        article3.setJournalists(article3journalists);
        article3.setPhotographers(article3photographers);
        article3.setPublication(publication1);
        article3 = articleRepository.save(article3);


        article4 = new Article();
        article4.setName("Artikkel 4");
        article4.setContent("Its not about how hard you can hit, its about hard you can GET hit - and keep on moving");
        Set<Person> article4journalists = new HashSet<>();
        Set<Person> article4photographers = new HashSet<>();
        article4photographers.add(person1);
        article4photographers.add(person2);
        article4.setJournalists(article4journalists);
        article4.setPhotographers(article4photographers);
        article4.setPublication(publication2);
        article4.setStatus(articleStatus1);
        article4 = articleRepository.save(article4);
    }



    @Test
    public void testSaveArticleUpdates() throws Exception {
        // Todo: Mock user and date
    }



    @Test
    public void testSaveArticleMetadata() throws Exception {
        Article article = new Article(article1.getId());
        ArticleStatus articleStatus1 = articleStatusRepository.save(new ArticleStatus("Desk", ""));
        ArticleType articleType1 = articleTypeRepository.save(new ArticleType("KulturRaport", ""));
        Publication publication1 = new Publication();
        publication1.setName("testpublication");
        publication1.setReleaseDate(new Date(114, 5, 5));
        publication1 = publicationRepository.save(publication1);

        Set<Person> journalists = new HashSet<>();
        Set<Person> photographers = new HashSet<>();
        photographers.add(personRepository.findOne(3L));

        article.setName("Updated name");
        article.setJournalists(journalists);
        article.setPhotographers(photographers);
        article.setContent("NEW CONTENT, SHOULD NOT BE CHANGED!");
        article.setComment("my cool comment");
        article.setStatus(articleStatus1);
        article.setType(articleType1);
        article.setPublication(publication1);

        article = articleService.saveMetadata(article);

        assertEquals("Updated name", article.getName());
        assertEquals(0, article.getJournalists().size());
        assertEquals(1, article.getPhotographers().size());
        assertEquals("my cool comment", article.getComment());
        assertEquals(articleStatus1.getName(), article.getStatus().getName());
        assertEquals(articleType1.getName(), article.getType().getName());
        assertEquals(publication1.getName(), article.getPublication().getName());

        assertEquals("Testinnhold for artikkel 1 yay", article.getContent());
    }

    @Test
    public void testSaveArticleContentsGeneratesARevision() throws Exception {
        Article article = new Article(article1.getId());
        article.setContent("NEW CONTENT for article 1");

        Article updated = articleService.saveNewContent(article);

        assertEquals("NEW CONTENT for article 1", updated.getContent());

        // Fetch the revision
        List<ArticleRevision> revisions = articleRevisionRepository.findByArticleIdOrderBySavedDateDesc(updated.getId());
        assertEquals(1, revisions.size());

        ArticleRevision rev = revisions.get(0);
        assertEquals("NEW CONTENT for article 1", rev.getContent());
    }

    @Test
    public void testEmptyArticleSearchReturnsAll() {
        ArticleSearchParams params = new ArticleSearchParams("", null, Collections.<Long>emptyList(), null, null);

        List<Article> expected = new ArrayList<>();
        expected.add(article1);
        expected.add(article2);
        expected.add(article3);
        expected.add(article4);


        List<Article> articles = articleService.searchForArticles(params);
        assertEquals(expected, articles);

        // just a check to see if the joins actually work, so that relations are populated
        assertTrue(articles.get(0).getJournalists().contains(new Person(1L)));
        assertTrue(articles.get(0).getJournalists().contains(new Person(2L)));
        assertEquals(2, articles.get(0).getJournalists().size());
    }

    @Test
    public void testSearchingForContent() {
        ArticleSearchParams params = new ArticleSearchParams("søke i dette", null, Collections.<Long>emptyList(), null, null);

        List<Article> expected = new ArrayList<>();
        expected.add(article2);

        assertEquals(expected, articleService.searchForArticles(params));
    }

    @Test
    public void testSearchingForPublication() {
        ArticleSearchParams params = new ArticleSearchParams("", null, Collections.<Long>emptyList(), null, article4.getPublication().getId());

        List<Article> expected = new ArrayList<>();
        expected.add(article4);

        List<Article> articles = articleService.searchForArticles(params);

        assertEquals(expected, articles);
    }

    @Test
    public void testSearchingForPerson() {
        ArticleSearchParams params1 = new ArticleSearchParams("", null, Arrays.asList(2L), null, null);
        ArticleSearchParams params2 = new ArticleSearchParams(null, null, Arrays.asList(1L,2L), null, null);
        ArticleSearchParams params3 = new ArticleSearchParams("", null, Arrays.asList(1L,2L,3L), null, null);

        List<Article> expected1 = new ArrayList<>();
        List<Article> expected2 = new ArrayList<>();
        List<Article> expected3 = new ArrayList<>();

        expected1.add(article1);
        expected1.add(article2);
        expected1.add(article3);
        expected1.add(article4);

        expected2.add(article1);
        expected2.add(article3);
        expected2.add(article4);

        List<Article> articles1 = articleService.searchForArticles(params1);
        List<Article> articles2 = articleService.searchForArticles(params2);

        assertEquals(expected1, articles1);
        assertEquals(expected2, articles2);
        assertEquals(expected3, articleService.searchForArticles(params3));
    }

    @Test
    public void testSearchingForBothPersonAndContent() {
        ArticleSearchParams params = new ArticleSearchParams("its about hard you can GET hit", null, Arrays.asList(1L, 2L), null,null);
        ArticleSearchParams params2 = new ArticleSearchParams("du", null, Arrays.asList(2L), null, null);

        List<Article> expected = new ArrayList<>();
        List<Article> expected2 = new ArrayList<>();
        expected.add(article4);

        expected2.add(article2);
        expected2.add(article3);

        List<Article> articles = articleService.searchForArticles(params);
        List<Article> articles2 = articleService.searchForArticles(params2);

        assertEquals(expected, articles);
        assertEquals(expected2, articles2);
    }

    @Test
    public void testSearchingForStatus() {
        ArticleSearchParams params = new ArticleSearchParams("",article4.getStatus().getId(),Collections.<Long>emptyList(),null,null);

        List<Article> expected = new ArrayList<>();
        expected.add(article4);

        List<Article> articles = articleService.searchForArticles(params);

        assertEquals(expected, articles);
    }

    @Test
    public void testSearchingForContentAndPersonAndStatusAndPublication() {
        ArticleSearchParams params = new ArticleSearchParams("moving",article4.getStatus().getId(),Arrays.asList(1L, 2L),null,article4.getPublication().getId());

        List<Article> expected = new ArrayList<>();
        expected.add(article4);

        List<Article> articles = articleService.searchForArticles(params);

        assertEquals(expected, articles);
    }
    //Trololololol, hilsen Petter Asla :-) Lykke til videre!
}
