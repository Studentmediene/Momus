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
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import liquibase.change.core.UpdateDataChange;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.*;

@Transactional
public class PublicationServiceTest extends AbstractTestRunner{
    @Mock
    PublicationRepository publicationRepository;

    @InjectMocks
    PublicationService publicationService;

    @Mock
    PageRepository pageRepository;

    @Mock
    ArticleRepository articleRepository;

    private Publication publication1;
    private Publication publication2;
    private Publication publication3;

    private Page page1;
    private Page page2;
    private Page page3;

    @Before
    public void before() throws Exception {
        publication1 = new Publication(1L);
        publication1.setName("DUSKEN1");

        publication2 = new Publication(2L);
        publication2.setName("DUSKEN2");

        publication3 = new Publication(3L);
        publication3.setName("DUSKEN3");

        page1 = new Page(1L);
        page1.setPageNr(1);
        page1.setPublication(publication1);
        page2 = new Page(2L);
        page2.setPageNr(2);
        page2.setPublication(publication1);
        page3 = new Page(3L);
        page3.setPageNr(3);        
        page3.setPublication(publication1);
    }

    /**
     * Method: {@link PublicationService#updatePublication(Publication)}
     */
    @Test
    public void testUpdatePublicationMetadata() throws Exception{
        PublicationService publicationServiceSpy = spy(publicationService);
        doReturn(publication1).when(publicationRepository).save(publication1);
        doReturn(publication1).when(publicationRepository).findOne(publication1.getId());
        publication1.setName("justanupdatedpubname");
        publication1 = publicationServiceSpy.updatePublication(publication1);

        verify(publicationRepository, times(1)).save(publication1);
        verify(publicationRepository, times(1)).findOne(publication1.getId());
        assertEquals("justanupdatedpubname",publication1.getName());
    }

    /**
     * Method: {@link PublicationService#getActivePublication(Date)}
     */
    @Test
    public void testGetActivePublication() throws Exception{
        publication1.setReleaseDate(new Date(2017, 8, 10)); //Old
        publication2.setReleaseDate(new Date(2017, 8, 12));
        publication3.setReleaseDate(new Date(2017, 8, 14));

        doReturn(Arrays.asList(publication2, publication2, publication1)).when(publicationRepository).findAllByOrderByReleaseDateDesc();

        assertEquals(publication2, publicationService.getActivePublication(new Date(2017, 8, 11)));
    }

    @Test
    public void testSavePage() throws Exception{
        final List<Page> pages = new ArrayList<>(Arrays.asList(page1, page2, page3));
        final Page newPage = new Page();
        newPage.setPublication(publication1);
        newPage.setPageNr(2);
        doAnswer((answer) -> {
            pages.add(newPage);
            return null;
        }).when(pageRepository).saveAndFlush(any(Page.class));
        doReturn(null).when(pageRepository).save(anyListOf(Page.class));

        doReturn(pages.stream().sorted().collect(Collectors.toList())).when(pageRepository).findByPublicationIdOrderByPageNrAsc(publication1.getId());

        publicationService.savePage(newPage);

        assertEquals(1, page1.getPageNr());
        assertEquals(2, newPage.getPageNr());
        assertEquals(3, page2.getPageNr());
        assertEquals(4, page3.getPageNr());
    }

    @Test
    public void testSaveTrailingPages() throws Exception{
        final List<Page> pages = new ArrayList<>(Arrays.asList(page1, page2, page3));
        Page newPage = new Page(4L);
        newPage.setPublication(publication1);
        newPage.setPageNr(2);
        Page otherNewPage = new Page(5L);
        newPage.setPublication(publication1);
        otherNewPage.setPageNr(3);

        List<Page> newPages = Arrays.asList(newPage, otherNewPage);

        doReturn(null).when(pageRepository).save(anyListOf(Page.class));

        doAnswer((answer) -> {
            pages.addAll(newPages);
            return null;
        }).when(pageRepository).save(newPages);

        doReturn(pages.stream().sorted().collect(Collectors.toList())).when(pageRepository).findByPublicationIdOrderByPageNrAsc(publication1.getId());

        publicationService.saveTrailingPages(Arrays.asList(newPage, otherNewPage));

        assertEquals(1, page1.getPageNr());
        assertEquals(2, newPage.getPageNr());
        assertEquals(3, otherNewPage.getPageNr());
        assertEquals(4, page2.getPageNr());
        assertEquals(5, page3.getPageNr());
    }

    @Test
    public void testUpdatePage() throws Exception {
        doReturn(null).when(pageRepository).saveAndFlush(any(Page.class));
        doReturn(null).when(pageRepository).save(anyListOf(Page.class));
        doReturn(new ArrayList<>(Arrays.asList(page1, page2, page3))
            .stream()
            .sorted()
            .collect(Collectors.toList())).when(pageRepository).findByPublicationIdOrderByPageNrAsc(publication1.getId());
        
        page3.setPageNr(2);

        publicationService.updatePage(page3);

        assertEquals(1, page1.getPageNr());
        assertEquals(2, page3.getPageNr());
        assertEquals(3, page2.getPageNr());
    }

    @Test
    public void testUpdateTrailingPages() throws Exception{
        doReturn(null).when(pageRepository).save(anyListOf(Page.class));
        doReturn(new ArrayList<>(Arrays.asList(page1, page2, page3))
            .stream()
            .sorted()
            .collect(Collectors.toList())).when(pageRepository).findByPublicationIdOrderByPageNrAsc(publication1.getId());
        
        page1.setPageNr(2);
        page2.setPageNr(3);
        publicationService.updateTrailingPages(Arrays.asList(page1, page2));

        assertEquals(1, page3.getPageNr());
        assertEquals(2, page1.getPageNr());
        assertEquals(3, page2.getPageNr());
    }

    @Test
    public void testDeletePage() throws Exception{
        doReturn(new ArrayList<>(Arrays.asList(page1, page2, page3))).when(pageRepository).findByPublicationIdOrderByPageNrAsc(publication1.getId());

        publicationService.deletePage(page2);
        assertEquals(1, page1.getPageNr());
        assertEquals(2, page3.getPageNr());
    }

    @Test
    public void testUpdatePageMetadata() throws Exception {
        doReturn(page1).when(pageRepository).saveAndFlush(page1);
        doReturn(page1).when(pageRepository).findOne(page1.getId());

        page1.setNote("vader is lukes father");
        page1.setWeb(true);
        page1.setAdvertisement(false);

        Page page = publicationService.updatePageMeta(page1);

        verify(pageRepository, times(1)).saveAndFlush(page1);
        assertEquals("vader is lukes father", page.getNote());
        assertEquals(true, page.isWeb());
        assertEquals(false, page.isAdvertisement());
    }
}
