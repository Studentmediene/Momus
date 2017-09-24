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
import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    private Article article1;

    private ArticleRevision article1Revision1;

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
        article1.setReview(articleReview1);
        article1.setSection(section1);
        article1.setArchived(false);

        article1Revision1 = new ArticleRevision();
        article1Revision1.setArticle(article1);
        article1Revision1.setContent(article1.getContent());
        article1Revision1.setStatus(article1.getStatus());

        when(articleRepository.findOne(article1.getId())).thenReturn(article1);
        when(articleRepository.saveAndFlush(any(Article.class))).then(returnsFirstArg());
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
        article.setReview(articleReview2);
        article.setSection(section2);
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
        assertEquals(articleReview2.getName(), article.getReview().getName());
        assertEquals(section2.getName(), article.getSection().getName());
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
}
