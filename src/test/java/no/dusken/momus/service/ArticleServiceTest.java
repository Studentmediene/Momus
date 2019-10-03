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
import no.dusken.momus.service.indesign.IndesignExport;
import no.dusken.momus.service.indesign.IndesignGenerator;
import no.dusken.momus.service.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.*;

@Transactional
public class ArticleServiceTest extends AbstractServiceTest {
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleRevisionRepository articleRevisionRepository;

    @Mock
    private ArticleStatusRepository articleStatusRepository;

    @Mock
    private ArticleReviewRepository articleReviewRepository;

    @Mock
    private IndesignGenerator indesignGenerator;

    @InjectMocks
    private ArticleService articleService;

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
        person1 = Person.builder()
                .id(2L)
                .guid(UUID.randomUUID())
                .username("mts")
                .name("Mats Matsessen")
                .active(true)
                .build();
        person2 = Person.builder()
                .id(3L)
                .guid(UUID.randomUUID())
                .username("kkr")
                .name("Kåre Kåressen")
                .active(true)
                .build();

        publication1 = Publication.builder()
                .name("Pub1")
                .build();
        publication1.setId(0L);

        publication2 = Publication.builder()
                .name("Pub2")
                .build();
        publication2.setId(1L);

        articleStatus1 = ArticleStatus.builder().name("Skrives").build();
        articleStatus1.setId(0L);
        articleStatus2 = ArticleStatus.builder().name("Til korrektur").build();
        articleStatus2.setId(1L);

        articleReview1 = ArticleReview.builder().name("Ukjent").build();
        articleReview1.setId(0L);
        articleReview2 = ArticleReview.builder().name("Ferdig").build();
        articleReview2.setId(1L);

        articleType1 = ArticleType.builder().name("Anmeldelse").build();
        articleType1.setId(0L);
        articleType2 = ArticleType.builder().name("Reportasje").build();
        articleType2.setId(1L);

        section1 = Section.builder().name("Musikk").build();
        section1.setId(0L);
        section2 = Section.builder().name("Forskning").build();
        section2.setId(1L);

        article1 = Article.builder()
                .name("Artikkel 1")
                .content("Testinnhold for artikkel 1 yay")
                .publication(publication1)
                .section(section1)
                .status(articleStatus1)
                .type(articleType1)
                .review(articleReview1)
                .archived(false)
                .build();
        article1.setId(1L);

        article2 = Article.builder()
                .name("Artikkel 2")
                .content("Lorem ipsum")
                .publication(publication1)
                .section(section1)
                .status(articleStatus1)
                .type(articleType1)
                .review(articleReview1)
                .archived(false)
                .build();
        article2.setId(2L);

        article1Revision1 = ArticleRevision.builder()
                .article(article1)
                .content(article1.getContent())
                .status(article1.getStatus())
                .build();
        article1Revision1.setId(0L);

        when(indesignGenerator.generateFromArticle(article1)).thenReturn(new IndesignExport("meh", "meh"));
        when(articleRepository.findById(article1.getId())).thenReturn(Optional.of(article1));
        when(articleRepository.saveAndFlush(any(Article.class))).then(returnsFirstArg());
        when(articleStatusRepository.findById(2L)).thenReturn(Optional.of(articleStatus1));
        when(articleReviewRepository.findById(1L)).thenReturn(Optional.of(articleReview1));
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
     * Method: {@link ArticleService#saveArticle}
     */
    @Test
    public void testCreateArticle() {
        article1 = articleService.createArticle(article1);

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
    public void testUpdateArticleMetadata() {
        Article article = Article.builder()
                .name("Updated name")
                .journalists(new HashSet<>(Arrays.asList(person1, person2)))
                .photographers(new HashSet<>(Arrays.asList(person1)))
                .content("NEW CONTENT, SHOULD NOT BE CHANGED!")
                .comment("my cool comment")
                .status(articleStatus2)
                .type(articleType2)
                .review(articleReview2)
                .section(section2)
                .publication(publication2)
                .build();
        article.setId(article1.getId());

        ArticleService articleServiceSpy = spy(articleService);
        doReturn(article1).when(articleServiceSpy).updateArticle(article);
        doReturn(new ArticleRevision()).when(articleServiceSpy).createRevision(article);

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
     * Method: {@link ArticleService#updateArticleStatus(Long, Article)}
     */
    @Test
    public void testUpdateArticleStatus() {
        Article article = Article.builder()
                .name("Updated name")
                .comment("my cool comment")
                .status(articleStatus2)
                .review(articleReview2)
                .build();
        article.setId(article1.getId());

        ArticleService articleServiceSpy = spy(articleService);
        doReturn(article1).when(articleServiceSpy).updateArticle(article);
        doReturn(new ArticleRevision()).when(articleServiceSpy).createRevision(article);

        article = articleServiceSpy.updateArticleStatus(article.getId(), article);

        verify(articleServiceSpy, times(1)).createRevision(article1);
        verify(articleServiceSpy, times(1)).updateArticle(article1);
        assertEquals("my cool comment", article.getComment());
        assertEquals(articleStatus2.getName(), article.getStatus().getName());
        assertEquals(articleReview2.getName(), article.getReview().getName());
    }

    /**
     * Method: {@link ArticleService#updateArticleContent(Article)}
     */
    @Test
    public void testUpdateArticleContent() {
        Article article = Article.builder().content("<p>NEW CONTENT for article 1</p>").build();
        article.setId(article1.getId());

        ArticleService articleServiceSpy = spy(articleService);
        doReturn(new ArticleRevision()).when(articleServiceSpy).createRevision(any(Article.class));

        article = articleServiceSpy.updateArticleContent(article1.getId(), "<p>NEW CONTENT for article 1</p>");

        verify(articleServiceSpy, times(1)).updateArticle(article1);
        verify(articleServiceSpy, times(1)).createRevision(article1);
        assertEquals("<p>NEW CONTENT for article 1</p>", article.getContent());
        assertEquals("new content for article 1", article.getRawcontent());
        assertEquals(25, article.getContentLength());
    }

    /**
     * Method: {@link ArticleService#updateArticleContent(Article)}
     * Verify that a new revision is not created when there is no change in content
     */
    @Test
    public void testUpdateArticleContentNoChange() {
        ArticleService articleServiceSpy = spy(articleService);

        articleServiceSpy.updateArticleContent(article1.getId(), article1.getContent());

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

        when(articleRevisionRepository.findFirstByArticleIdOrderBySavedDateDesc(article1.getId())).thenReturn(Optional.of(article1Revision1));
        when(articleRevisionRepository.save(any(ArticleRevision.class))).then(returnsFirstArg());

        // Test too old previous revision creates new
        ZonedDateTime c = ZonedDateTime.now();
        c = c.minusDays(1);

        article1Revision1.setSavedDate(c);
        article1Revision1.setStatusChanged(false);

        assertTrue(articleServiceSpy.createRevision(article1) != article1Revision1);

        // Test young previous revision but changed status creates new
        c = ZonedDateTime.now();
        c = c.minusHours(1);

        article1Revision1.setSavedDate(c);
        article1Revision1.setStatusChanged(true);

        assertTrue(articleServiceSpy.createRevision(article1) != article1Revision1);

        // Test young previous revision and not changed status reuses old
        c = ZonedDateTime.now();
        c = c.minusMinutes(1);

        article1Revision1.setSavedDate(c);
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
        assertEquals(true, article.isArchived());
    }
}
