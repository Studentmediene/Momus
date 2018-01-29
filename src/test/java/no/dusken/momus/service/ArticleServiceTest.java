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

import com.google.api.services.drive.model.File;
import no.dusken.momus.model.*;
import no.dusken.momus.service.drive.GoogleDriveService;
import no.dusken.momus.service.indesign.IndesignExport;
import no.dusken.momus.service.indesign.IndesignGenerator;
import no.dusken.momus.service.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.*;

@Transactional
public class ArticleServiceTest extends AbstractServiceTest {
    @Mock
    ArticleRepository articleRepository;

    @Mock
    ArticleRevisionRepository articleRevisionRepository;

    @Mock
    GoogleDriveService googleDriveService;

    @Mock
    IndesignGenerator indesignGenerator;

    @InjectMocks
    ArticleService articleService;

    private Person person1;
    private Person person2;

    private Article article1;
    private Article article2;

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
    public void setUp() {
        person1 = new Person(2L, UUID.randomUUID(), "mts", "Mats Matsessen", "", "", true);
        person2 = new Person(3L, UUID.randomUUID(), "aaa", "Kåre Kåressen", "", "", true);

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
        article1.setArchived(false);

        article2 = new Article(2L);
        article2.setName("Artikkel 2");
        article2.setContent("Lorem ipsum");
        article2.setPublication(publication1);
        article2.setSection(section1);
        article2.setStatus(articleStatus1);
        article2.setType(articleType1);
        article2.setReview(articleReview1);
        article2.setArchived(false);

        article1Revision1 = new ArticleRevision(0L);
        article1Revision1.setArticle(article1);
        article1Revision1.setContent(article1.getContent());
        article1Revision1.setStatus(article1.getStatus());

        when(indesignGenerator.generateFromArticle(article1)).thenReturn(new IndesignExport("meh", "meh"));
        when(googleDriveService.createDocument(anyString())).thenReturn(new File());
        when(articleRepository.exists(longThat(i -> i == 1L || i == 2L ))).thenReturn(true);
        when(articleRepository.findOne(article1.getId())).thenReturn(article1);
        when(articleRepository.findOne(article2.getId())).thenReturn(article2);
        when(articleRepository.saveAndFlush(any(Article.class))).then(returnsFirstArg());
    }

    /**
     * Method: {@link ArticleService#getArticleById}
     */
    @Test
    public void testGetArticleById() {
        Article article = articleService.getArticleById(1L);
        assert(article.getId() == 1L);
    }

    /**
     * Method: {@link ArticleService#getArticlesByIds}
     */
    @Test
    public void testGetArticlesByIds() {
        List<Article> articles = articleService.getArticlesByIds(new ArrayList<>(Arrays.asList(1L, 2L)));
        assert(articles.size() == 2);
    }

    /**
     * Method: {@link ArticleService#saveArticle}
     */
    @Test
    public void testSaveArticle() {
        article1 = articleService.saveArticle(article1);

        verify(googleDriveService, times(1)).createDocument(article1.getName());
        verify(articleRepository, times(1)).saveAndFlush(article1);
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
     * Method: {@link ArticleService#updateArticleMetadata(Long, Article)}
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

        article = articleServiceSpy.updateArticleMetadata(article.getId(), article);

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
    public void testUpdateArticleContent() {
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
     * Method: {@link ArticleService#updateArticleContent(Article)}
     * Verify that a new revision is not created when there is no change in content
     */
    @Test
    public void testUpdateArticleContentNoChange() {
        Article article = new Article(article1.getId());
        article.setContent(article1.getContent());
        ArticleService articleServiceSpy = spy(articleService);

        articleServiceSpy.updateArticleContent(article);

        verify(articleServiceSpy, times(0)).updateArticle(article1);
        verify(articleServiceSpy, times(0)).createRevision(article1);
    }

    /**
     * Method: {@link ArticleService#exportArticle}
     */
    @Test
    public void testExportArticle() {
        articleService.exportArticle(article1.getId());

        verify(indesignGenerator, times(1)).generateFromArticle(article1);
    }

    /**
     * Method: {@link ArticleService#createRevision(Article)}
     */
    @Test
    public void testCreateRevision() {
        ArticleService articleServiceSpy = spy(articleService);

        when(articleRevisionRepository.findOne(any(Long.class))).thenReturn(article1Revision1);
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
     * Method: {@link ArticleService#updateNote}
     */
    @Test
    public void testUpdateNote() {
        ArticleService articleServiceSpy = spy(articleService);
        doReturn(article1).when(articleServiceSpy).updateArticle(article1);

        Article article = articleServiceSpy.updateNote(article1.getId(), "New note");

        verify(articleServiceSpy, times(1)).updateArticle(article1);
        assertEquals("New note", article.getNote());
    }

    /**
     * Method: {@link ArticleService#updateArchived}
     */
    @Test
    public void testUpdateArchived() {
        ArticleService articleServiceSpy = spy(articleService);
        doReturn(article1).when(articleServiceSpy).updateArticle(article1);

        Article article = articleServiceSpy.updateArchived(article1.getId(), true);

        verify(articleServiceSpy, times(1)).updateArticle(article1);
        assertEquals(true, article.getArchived());
    }
}
