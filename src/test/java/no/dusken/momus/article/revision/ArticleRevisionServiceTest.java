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

package no.dusken.momus.article.revision;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.transaction.annotation.Transactional;

import no.dusken.momus.common.AbstractServiceTest;
import no.dusken.momus.article.Article;
import no.dusken.momus.article.ArticleReviewStatus;
import no.dusken.momus.article.ArticleStatus;
import no.dusken.momus.article.revision.ArticleRevision;
import no.dusken.momus.article.revision.ArticleRevisionRepository;
import no.dusken.momus.article.revision.ArticleRevisionService;
import no.dusken.momus.article.type.ArticleType;
import no.dusken.momus.publication.Publication;
import no.dusken.momus.section.Section;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.*;

@Transactional
public class ArticleRevisionServiceTest extends AbstractServiceTest {
    @Mock
    private ArticleRevisionRepository articleRevisionRepository;

    @InjectMocks
    private ArticleRevisionService articleRevisionService;

    private Article article1;
    private Article article2;

    private ArticleRevision article1Revision1;

    private Publication publication1;
    private Publication publication2;

    private ArticleStatus articleStatus;

    private ArticleReviewStatus articleReviewStatus;

    private ArticleType articleType1;
    private ArticleType articleType2;

    private Section section1;
    private Section section2;

    @Before
    public void setUp() {
        publication1 = Publication.builder()
                .name("Pub1")
                .build();
        publication1.setId(0L);

        publication2 = Publication.builder()
                .name("Pub2")
                .build();
        publication2.setId(1L);

        articleStatus = ArticleStatus.UNKNOWN;

        articleReviewStatus = ArticleReviewStatus.PRINTED;

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
                .status(articleStatus)
                .type(articleType1)
                .review(articleReviewStatus)
                .archived(false)
                .build();
        article1.setId(1L);

        article2 = Article.builder()
                .name("Artikkel 2")
                .content("Lorem ipsum")
                .publication(publication1)
                .section(section1)
                .status(articleStatus)
                .type(articleType1)
                .review(articleReviewStatus)
                .archived(false)
                .build();
        article2.setId(2L);

        article1Revision1 = ArticleRevision.builder()
                .article(article1)
                .content(article1.getContent())
                .status(article1.getStatus())
                .build();
        article1Revision1.setId(0L);
    }

    /**
     * Method: {@link ArticleRevisionService#createRevision(Article)}
     */
    @Test
    public void testCreateRevision() {
        ArticleRevisionService articleRevisionServiceSpy = spy(articleRevisionService);

        when(articleRevisionRepository.findFirstByArticleIdOrderBySavedDateDesc(article1.getId())).thenReturn(Optional.of(article1Revision1));
        when(articleRevisionRepository.save(any(ArticleRevision.class))).then(returnsFirstArg());

        // Test too old previous revision creates new
        ZonedDateTime c = ZonedDateTime.now();
        c = c.minusDays(1);

        article1Revision1.setSavedDate(c);
        article1Revision1.setStatusChanged(false);

        assertTrue(articleRevisionServiceSpy.createRevision(article1) != article1Revision1);

        // Test young previous revision but changed status creates new
        c = ZonedDateTime.now();
        c = c.minusHours(1);

        article1Revision1.setSavedDate(c);
        article1Revision1.setStatusChanged(true);

        assertTrue(articleRevisionServiceSpy.createRevision(article1) != article1Revision1);

        // Test young previous revision and not changed status reuses old
        c = ZonedDateTime.now();
        c = c.minusMinutes(1);

        article1Revision1.setSavedDate(c);
        article1Revision1.setStatusChanged(false);

        assertTrue(articleRevisionServiceSpy.createRevision(article1) == article1Revision1);
    }
}
