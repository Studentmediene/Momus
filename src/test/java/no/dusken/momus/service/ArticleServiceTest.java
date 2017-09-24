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

package no.dusken.momus.service;

import no.dusken.momus.model.*;
import no.dusken.momus.service.repository.*;
import no.dusken.momus.service.search.ArticleSearchParams;
import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.*;

@Transactional
public class ArticleServiceTest extends AbstractTestRunner {

    @Mock
    PersonRepository personRepository;

    @Mock
    ArticleRepository articleRepository;

    @Mock
    PublicationRepository publicationRepository;

    @Mock
    ArticleStatusRepository articleStatusRepository;

    @Mock
    ArticleTypeRepository articleTypeRepository;

    @Mock
    ArticleRevisionRepository articleRevisionRepository;

    @Mock
    ArticleReviewRepository articleReviewRepository;

    @Mock
    SectionRepository sectionRepository;

    @InjectMocks
    ArticleService articleService;

    private Person person1;
    private Person person2;
    private Person person3;

    private Article article1;
    private Article article2;
    private Article article3;
    private Article article4;

    private ArticleRevision article1Revision1;
    private ArticleRevision article1Revision2;

    private Publication publication1;
    private Publication publication2;

    private ArticleStatus articleStatus1;
    private ArticleStatus articleStatus2;

    private ArticleReview articleReview1;
    private ArticleReview articleReview2;

    private ArticleType articleType1;
    private ArticleType articleType2;

    private Section section1;
    private Section section2;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        person1 = new Person(1L, "mts", "Mats", "Matsessen", "", "", true);
        person2 = new Person(2L, "aaa", "Kåre", "Kåressen", "", "", true);
        person3 = new Person(3L, "bbb", "Flaks", "Flaksesen", "", "", true);

        publication1 = new Publication(1L);
        publication1.setName("Pub1");
        publication2 = new Publication(2L);
        publication2.setName("Pub2");

        articleStatus1 = new ArticleStatus(0L, "Skrives", "");
        articleStatus2 = new ArticleStatus(1L, "Til korrektur", "");

        articleReview1 = new ArticleReview(0L, "Ukjent", "");
        articleReview2 = new ArticleReview(1L, "Ferdig", "");

        articleType1 = new ArticleType(0L, "Anmeldelse", "");
        articleType2 = new ArticleType(1L, "Reportasje", "");

        section1 = new Section(0L, "Musikk");
        section2 = new Section(0L, "Forskning");

        article1 = new Article(1L);
        article1.setName("Artikkel 1");
        article1.setContent("Testinnhold for artikkel 1 yay");
        article1.setPublication(publication1);
        article1.setSection(section1);
        article1.setStatus(articleStatus1);
        article1.setType(articleType1);
        article1.setArchived(false);

        article2 = new Article(2L);
        article2.setName("Artikkel 2");
        article2.setContent("Masse kult innhold, kan du søke i dette kanskje??");
        article2.setJournalists(new HashSet<>(Arrays.asList(person2)));
        article2.setPhotographers(new HashSet<>(Arrays.asList(person3)));
        article2.setPublication(publication1);
        article2.setSection(section1);
        article2.setArchived(false);

        article3 = new Article(3L);
        article3.setName("Artikkel 3");
        article3.setContent("Hei på deg, flott du leser testene! :)");
        article3.setJournalists(new HashSet<>(Arrays.asList(person1)));
        article3.setPhotographers(new HashSet<>(Arrays.asList(person2)));
        article3.setPublication(publication1);
        article2.setSection(section1);
        article3.setArchived(false);

        article4 = new Article(4L);
        article4.setName("Artikkel 4");
        article4.setContent("Its not about how hard you can hit, its about hard you can GET hit - and keep on moving");
        Set<Person> article4journalists = new HashSet<>();
        Set<Person> article4photographers = new HashSet<>();
        article4photographers.add(person1);
        article4photographers.add(person2);
        article4.setJournalists(article4journalists);
        article4.setPhotographers(article4photographers);
        article4.setPublication(publication2);
        article4.setReview(articleReview1);
        article4.setStatus(articleStatus1);
        article4.setSection(section1);
        article4.setArchived(false);

        article1Revision1 = new ArticleRevision();
        article1Revision1.setArticle(article1);
        article1Revision1.setContent(article1.getContent());
        article1Revision1.setStatus(article1.getStatus());

        when(articleRepository.findOne(article1.getId())).thenReturn(article1);
        //when(articleRepository.findOne(article2.getId())).thenReturn(article2);
        //when(articleRepository.findOne(article3.getId())).thenReturn(article3);
        //when(articleRepository.findOne(article4.getId())).thenReturn(article4);

        when(articleRepository.saveAndFlush(any(Article.class))).then(returnsFirstArg());
        
    }

    @Test
    public void testSaveArticleUpdates() throws Exception {
        // Todo: Mock user and date
    }

    /**
     * Method: {@link ArticleService#updateArticle(Article)}
     */
    @Test
    public void testUpdateArticle() {
        article1 = articleService.updateArticle(article1);

        verify(articleRepository, times(1)).saveAndFlush(article1);
        assertTrue(article1.getLastUpdated() != null); // Check that last
    }

    /**
     * Method: {@link ArticleService#updateArticleMetadata(Article)}
     */
    @Test
    public void testUpdateArticleMetadata() throws Exception {
        Article article = new Article(article1.getId());
        ArticleService articleServiceSpy = spy(articleService);

        doReturn(article1).when(articleServiceSpy).updateArticle(article);
        doReturn(new ArticleRevision()).when(articleServiceSpy).createRevision(article);

        article.setName("Updated name");
        article.setJournalists(new HashSet<>(Arrays.asList(person1, person2)));
        article.setPhotographers(new HashSet<>(Arrays.asList(person1)));
        article.setContent("NEW CONTENT, SHOULD NOT BE CHANGED!");
        article.setComment("my cool comment");
        article.setStatus(articleStatus2);
        article.setType(articleType2);
        article.setPublication(publication2);

        article = articleServiceSpy.updateArticleMetadata(article);

        verify(articleServiceSpy, times(1)).createRevision(article1);
        verify(articleServiceSpy, times(1)).updateArticle(article1);
        assertEquals("Updated name", article.getName());
        assertEquals(2, article.getJournalists().size());
        assertEquals(1, article.getPhotographers().size());
        assertEquals("my cool comment", article.getComment());
        assertEquals(articleStatus2.getName(), article.getStatus().getName());
        assertEquals(articleType2.getName(), article.getType().getName());
        assertEquals(publication2.getName(), article.getPublication().getName());
        assertEquals("Testinnhold for artikkel 1 yay", article.getContent());
    }

    /**
     * Method: {@link ArticleService#updateArticleContent(Article)}
     */
    @Test
    public void testUpdateArticleContent() throws Exception {
        Article article = new Article(article1.getId());
        ArticleService articleServiceSpy = spy(articleService);

        doReturn(new ArticleRevision()).when(articleServiceSpy).createRevision(any(Article.class));
        doReturn(article1).when(articleServiceSpy).updateArticle(article);

        article.setContent("NEW CONTENT for article 1");

        article = articleServiceSpy.updateArticleContent(article);

        verify(articleServiceSpy, times(1)).updateArticle(article1);
        verify(articleServiceSpy, times(1)).createRevision(article1);
        assertEquals("NEW CONTENT for article 1", article.getContent());
    }

    /**
     * Method: {@link ArticleService#createRevision(Article)}
     */
    @Test
    public void testCreateRevision() throws Exception {
        ArticleService articleServiceSpy = spy(articleService);

        when(articleRevisionRepository.findByArticleIdOrderBySavedDateDesc(article1.getId())).thenReturn(Collections.singletonList(article1Revision1));
        when(articleRevisionRepository.save(any(ArticleRevision.class))).then(returnsFirstArg());

        // Test too old previous revision creates new
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);

        article1Revision1.setSavedDate(c.getTime());
        article1Revision1.setStatusChanged(false);

        assertTrue(articleServiceSpy.createRevision(article1) != article1Revision1);

        // Test young previous revision but changed status creates new
        c = Calendar.getInstance();
        c.add(Calendar.HOUR, -1);

        article1Revision1.setSavedDate(c.getTime());
        article1Revision1.setStatusChanged(true);

        assertTrue(articleServiceSpy.createRevision(article1) != article1Revision1);

        // Test young previous revision and not changed status reuses old
        c = Calendar.getInstance();
        c.add(Calendar.MINUTE, -1);

        article1Revision1.setSavedDate(c.getTime());
        article1Revision1.setStatusChanged(false);

        assertTrue(articleServiceSpy.createRevision(article1) == article1Revision1);
    }

    /**
     * Method: {@link ArticleService#updateNote()}
     */
    @Test
    public void testUpdateNote() {
        ArticleService articleServiceSpy = spy(articleService);
        doReturn(article1).when(articleServiceSpy).updateArticle(article1);

        Article article = articleServiceSpy.updateNote(article1, "New note");

        verify(articleServiceSpy, times(1)).updateArticle(article1);
        assertEquals("New note", article.getNote());
    }

    /**
     * Method: {@link ArticleService#updateArchived())}
     */
    @Test
    public void testUpdateArchived() {
        ArticleService articleServiceSpy = spy(articleService);
        doReturn(article1).when(articleServiceSpy).updateArticle(article1);

        Article article = articleServiceSpy.updateArchived(article1, true);

        verify(articleServiceSpy, times(1)).updateArticle(article1);
        assertEquals(true, article.getArchived());
    }

    /*
    @Test
    public void testEmptyArticleSearchReturnsAll() {
        ArticleSearchParams params = new ArticleSearchParams("", null, Collections.<Long>emptyList(),null, null, null, 20, 1, false);

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
        ArticleSearchParams params = new ArticleSearchParams("kåre dettE SøkE", null, Collections.<Long>emptyList(),null, null, null, 100, 1, false);

        List<Article> expected = new ArrayList<>();
        expected.add(article2);

        assertEquals(expected, articleService.searchForArticles(params));
    }

    @Test
    public void testSearchingForPublication() {
        ArticleSearchParams params = new ArticleSearchParams("", null, Collections.<Long>emptyList(), null,null, article4.getPublication().getId(), 0, 0, false);

        List<Article> expected = new ArrayList<>();
        expected.add(article4);

        List<Article> articles = articleService.searchForArticles(params);

        assertEquals(expected, articles);
    }

    @Test
    public void testSearchingForPerson() {
        ArticleSearchParams params1 = new ArticleSearchParams("", null, Arrays.asList(2L), null,null, null, 20, 1, false);
        ArticleSearchParams params2 = new ArticleSearchParams(null, null, Arrays.asList(1L,2L),null, null, null, 20, 1, false);
        ArticleSearchParams params3 = new ArticleSearchParams("", null, Arrays.asList(1L,2L,3L),null, null, null, 20, 1, false);

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
        ArticleSearchParams params = new ArticleSearchParams("its about hard you can GET hit", null, Arrays.asList(1L, 2L), null,null,null, 20, 1, false);
        ArticleSearchParams params2 = new ArticleSearchParams("du", null, Arrays.asList(2L), null, null, null, 20, 1, false);

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
        ArticleSearchParams params = new ArticleSearchParams("",article4.getStatus().getId(),Collections.<Long>emptyList(),null,null,null, 100, 1, false);

        List<Article> expected = new ArrayList<>();
        expected.add(article1);
        expected.add(article4);

        List<Article> articles = articleService.searchForArticles(params);

        assertEquals(expected, articles);
    }

    @Test
    public void testSearchingForReview() {
        ArticleSearchParams params = new ArticleSearchParams("",null,Collections.<Long>emptyList(),article4.getReview().getId(),null,null, 100, 1, false);

        List<Article> expected = new ArrayList<>();
        expected.add(article4);

        List<Article> articles = articleService.searchForArticles(params);

        assertEquals(expected, articles);
    }

    @Test
    public void testSearchingForContentAndPersonAndStatusAndPublication() {
        ArticleSearchParams params = new ArticleSearchParams("moving",article4.getStatus().getId(),Arrays.asList(1L, 2L),null,null,article4.getPublication().getId(), 100, 1, false);

        List<Article> expected = new ArrayList<>();
        expected.add(article4);

        List<Article> articles = articleService.searchForArticles(params);

        assertEquals(expected, articles);
    }
    */
    //Trololololol, hilsen Petter Asla :-) Lykke til videre!
}
