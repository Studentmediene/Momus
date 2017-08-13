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

import no.dusken.momus.model.Article;
import no.dusken.momus.model.Page;
import no.dusken.momus.model.Publication;
import no.dusken.momus.service.PublicationService;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.PageRepository;
import no.dusken.momus.service.repository.PublicationRepository;
import no.dusken.momus.test.AbstractTestRunner;

import org.apache.commons.ssl.TomcatServerXML;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;

@Transactional
public class PublicationServiceTest extends AbstractTestRunner{
    @Autowired
    PublicationRepository publicationRepository;

    @Autowired
    PublicationService publicationService;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    ArticleRepository articleRepository;

    private Publication publication1;
    private Publication publication2;
    private Publication publication3;

    private Page page1;
    private Page page2;
    private Page page3;

    @Before
    public void before() throws Exception {
        publication1 = new Publication();
        publication1.setName("DUSKEN1");

        publication2 = new Publication();
        publication2.setName("DUSKEN2");

        publication3 = new Publication();
        publication3.setName("DUSKEN3");

        publication1 = publicationRepository.save(publication1);
        publication2 = publicationRepository.save(publication2);
        publication3 = publicationRepository.save(publication3);

        page1 = new Page();
        page1.setPageNr(1);
        page1.setPublication(publication1);
        page2 = new Page();
        page2.setPageNr(2);
        page2.setPublication(publication1);
        page3 = new Page();
        page3.setPageNr(3);        
        page3.setPublication(publication1);
        
        page1 = pageRepository.save(page1);
        page2 = pageRepository.save(page2);
        page3 = pageRepository.save(page3);
    }

    @Test
    public void testUpdatePublicationMetadata() throws Exception{
        publication1.setName("justanupdatedpubname");
        publication1 = publicationService.updatePublication(publication1);

        assertEquals("justanupdatedpubname",publication1.getName());
    }

    @Test
    public void testGetActivePublication() throws Exception{
        publication1.setReleaseDate(new Date(2017, 8, 10)); //Old
        publicationRepository.save(publication1);

        publication2.setReleaseDate(new Date(2017, 8, 12));
        publicationRepository.save(publication2);

        publication3.setReleaseDate(new Date(2017, 8, 14));
        publicationRepository.save(publication3);

        assertEquals(publication2, publicationService.getActivePublication(new Date(2017, 8, 11)));
    }

    @Test
    public void testSavePage() throws Exception{
        Page newPage = new Page();
        newPage.setPublication(publication1);
        newPage.setPageNr(2);
        newPage = publicationService.savePage(newPage);

        assertEquals(1, pageRepository.findOne(page1.getId()).getPageNr());
        assertEquals(2, pageRepository.findOne(newPage.getId()).getPageNr());
        assertEquals(3, pageRepository.findOne(page2.getId()).getPageNr());
        assertEquals(4, pageRepository.findOne(page3.getId()).getPageNr());
    }

    @Test
    public void testUpdatePage() throws Exception{
        page3.setPageNr(2);
        publicationService.updatePage(page3);

        assertEquals(1, pageRepository.findOne(page1.getId()).getPageNr());
        assertEquals(2, pageRepository.findOne(page3.getId()).getPageNr());
        assertEquals(3, pageRepository.findOne(page2.getId()).getPageNr());
    }

    @Test
    public void testDeletePage() throws Exception{
        publicationService.deletePage(page2);

        assertEquals(1, pageRepository.findOne(page1.getId()).getPageNr());
        assertEquals(2, pageRepository.findOne(page3.getId()).getPageNr());
    }

    @Test
    public void testUpdatePageMetadata() throws Exception{        
        page1.setNote("vader is lukes father");
        page1.setWeb(true);
        page1.setAdvertisement(false);
        page1 = publicationService.updatePage(page1);

        assertEquals("vader is lukes father", page1.getNote());
        assertEquals(true, page1.isWeb());
        assertEquals(false, page1.isAdvertisement());
    }

    @Test
    public void testUpdateArticlesPage() throws Exception{        
        Article a = new Article(1L);
        a = articleRepository.save(a);
        Set<Article> articles = new HashSet<>();
        articles.add(a);
        page1.setArticles(articles);
        page1 = publicationService.updatePage(page1);

        assertEquals(page1.getArticles(),articles);

        articles.remove(a);
        page1.setArticles(articles);
        page1 = publicationService.updatePage(page1);

        assertEquals(articles, page1.getArticles());
    }
}
