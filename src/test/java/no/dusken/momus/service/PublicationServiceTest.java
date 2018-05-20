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
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.LayoutStatusRepository;
import no.dusken.momus.service.repository.PageRepository;
import no.dusken.momus.service.repository.PublicationRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@Transactional
public class PublicationServiceTest extends AbstractServiceTest {
    @Mock
    private PublicationRepository publicationRepository;

    @InjectMocks
    private PublicationService publicationService;

    @Mock
    private PageRepository pageRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private LayoutStatusRepository layoutStatusRepository;

    private Publication publication1;
    private Publication publication2;
    private Publication publication3;

    private Page page1;
    private Page page2;
    private Page page3;

    @Before
    public void before() {
        publication1 = Publication.builder().name("DUSKEN1").build();
        publication1.setId(1L);
        publication2 = Publication.builder().name("DUSKEN2").build();
        publication2.setId(2L);
        publication3 = Publication.builder().name("DUSKEN3").build();
        publication3.setId(3L);

        page1 = Page.builder().pageNr(1).publication(publication1).build();
        page1.setId(0L);
        page2 = Page.builder().pageNr(2).publication(publication1).build();
        page2.setId(1L);
        page3 = Page.builder().pageNr(2).publication(publication1).build();
        page3.setId(2L);
    }

    /**
     * Method: {@link PublicationService#updatePublication(Publication)}
     */
    @Test
    public void testUpdatePublicationMetadata() {
        PublicationService publicationServiceSpy = spy(publicationService);
        doReturn(publication1).when(publicationRepository).saveAndFlush(publication1);
        publication1.setName("justanupdatedpubname");
        publication1 = publicationServiceSpy.updatePublication(publication1);

        verify(publicationRepository, times(1)).saveAndFlush(publication1);
        assertEquals("justanupdatedpubname",publication1.getName());
    }

    /**
     * Method: {@link PublicationService#getActivePublication(LocalDate)}
     */
    @Test
    public void testGetActivePublication() {
        publication1.setReleaseDate(LocalDate.of(2017, 8, 10)); //Old
        publication2.setReleaseDate(LocalDate.of(2017, 8, 12));
        publication3.setReleaseDate(LocalDate.of(2017, 8, 14));

        doReturn(Arrays.asList(publication2, publication2, publication1)).when(publicationRepository).findAllByOrderByReleaseDateDesc();

        assertEquals(publication2, publicationService.getActivePublication(LocalDate.of(2017, 8, 11)));
    }

    @Test
    public void testSavePage() {
        final List<Page> pages = new ArrayList<>(Arrays.asList(page1, page2, page3));
        final Page newPage = new Page();
        newPage.setPublication(publication1);
        newPage.setPageNr(2);
        doAnswer((answer) -> {
            pages.add(newPage);
            return null;
        }).when(pageRepository).saveAndFlush(any(Page.class));
        doReturn(null).when(pageRepository).save(anyList());

        doReturn(pages.stream().sorted().collect(Collectors.toList())).when(pageRepository).findByPublicationIdOrderByPageNrAsc(publication1.getId());

        publicationService.savePage(newPage);

        assertEquals(1, page1.getPageNr());
        assertEquals(2, newPage.getPageNr());
        assertEquals(3, page2.getPageNr());
        assertEquals(4, page3.getPageNr());
    }

    @Test
    public void testSaveTrailingPages() {
        final List<Page> pages = new ArrayList<>(Arrays.asList(page1, page2, page3));
        Page newPage = Page.builder().publication(publication1).pageNr(2).build();
        Page otherNewPage = Page.builder().pageNr(3).publication(publication1).build();

        List<Page> newPages = Arrays.asList(newPage, otherNewPage);

        doReturn(null).when(pageRepository).save(anyList());

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
    public void testUpdateTrailingPages(){
        doReturn(null).when(pageRepository).save(anyList());
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
    public void testDeletePage(){
        doReturn(new ArrayList<>(Arrays.asList(page1, page2, page3))).when(pageRepository).findByPublicationIdOrderByPageNrAsc(publication1.getId());

        publicationService.deletePage(page2);
        assertEquals(1, page1.getPageNr());
        assertEquals(2, page3.getPageNr());
    }

    @Test
    public void testUpdatePageMetadata() {
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

    @Test
    public void testSavePublication() {
        doReturn(publication1).when(publicationRepository).save(publication1);
        doReturn(new LayoutStatus()).when(layoutStatusRepository).findByName("Ukjent");

        publicationService.savePublication(publication1, 50);
        verify(publicationRepository, times(1)).save(publication1);
        verify(pageRepository, times(50)).save(any(Page.class));
    }

    @Test
    public void testGenerateColophon() {
        Article art = Article.builder()
                .useIllustration(false)
                .journalists(new HashSet<>(Arrays.asList(
                        Person.builder().id(0L).name("Eiv").build(),
                        Person.builder().id(1L).name("Chr").build())))
                .photographers(new HashSet<>(Arrays.asList(
                        Person.builder().id(2L).name("Don").build(),
                        Person.builder().id(3L).name("Oba").build())))
                .build();

        Article art2 = Article.builder()
                .useIllustration(true)
                .journalists(new HashSet<>())
                .photographers(Collections.singleton(Person.builder().id(4L).name("Ill").build()))
                .build();

        doReturn(Arrays.asList(art, art2)).when(articleRepository).findByPublicationId(publication1.getId());

        String colophon = publicationService.generateColophon(publication1.getId());


        String expected = "Journalister:\r\nEiv\r\nChr\r\n\r\nFotografer:\r\nDon\r\nOba\r\n\r\nIllustratører:\r\nIll\r\n";

        assertEquals(expected, colophon);
    }
}
