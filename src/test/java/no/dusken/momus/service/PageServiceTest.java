package no.dusken.momus.service;

import no.dusken.momus.dto.PageContent;
import no.dusken.momus.model.*;
import no.dusken.momus.model.websocket.Action;
import no.dusken.momus.service.repository.AdvertRepository;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.LayoutStatusRepository;
import no.dusken.momus.service.repository.PageRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PageServiceTest extends AbstractServiceTest {

    @InjectMocks
    private PageService pageService;

    @Mock
    private PageRepository pageRepository;

    @Mock
    private LayoutStatusRepository layoutStatusRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private AdvertRepository advertRepository;

    private Publication publication;

    @Before
    public void before() {
        publication = Publication.builder().name("D1").build();
        publication.setId(0L);

        when(layoutStatusRepository.findByName(anyString())).thenReturn(new LayoutStatus());
        when(pageRepository.getPageOrderByPublicationId(anyLong())).thenReturn(new ArrayList<>());
    }

    @Test
    public void createEmptyPagesInPublication() {
        PageService pageServiceSpy = spy(pageService);

        doAnswer(invocationOnMock -> {
            Page p = invocationOnMock.getArgument(0);
            p.setId((long) p.getPageNr());
            return p;
        }).when(pageRepository).save(any(Page.class));

        pageServiceSpy.createEmptyPagesInPublication(publication.getId(), 0, 10);

        verify(pageServiceSpy, times(1)).setPageOrder(any(), eq(publication.getId()));
        verify(pageRepository, times(10)).save(any(Page.class));
        verify(messagingService, times(10)).broadcastEntityAction(any(Page.class), eq(Action.CREATE));

    }

    @Test
    public void updateMetadata() {
        LayoutStatus l = new LayoutStatus();
        Page existing = new Page();
        existing.setId(0L);

        when(pageRepository.findOne(0L)).thenReturn(existing);
        when(pageRepository.saveAndFlush(existing)).thenReturn(existing);

        Page p = Page.builder().layoutStatus(l).done(true).build();
        existing = pageService.updateMetadata(0L, p);

        verify(pageRepository, times(1)).saveAndFlush(existing);
        verify(messagingService, times(1)).broadcastEntityAction(existing, Action.UPDATE);
        assertEquals(l, existing.getLayoutStatus());
        assertEquals(true, existing.isDone());
    }

    @Test
    public void setPageOrder() {
        List<Long> pageOrder = new ArrayList<>(Arrays.asList(0L, 1L, 4L, 3L));

        pageService.setPageOrder(pageOrder, publication.getId());

        verify(pageRepository, times(pageOrder.size())).updatePageNr(anyInt(), anyLong());
        verify(messagingService, times(1)).broadcastEntityAction(any(), eq(Action.UPDATE));
    }

    @Test
    public void setContent() {
        Page p = Page.builder().publication(publication).build();
        p.setId(0L);
        List<Article> articles = new ArrayList<>(Collections.singletonList(Article.builder().build()));
        List<Advert> adverts = new ArrayList<>(Collections.singletonList(Advert.builder().build()));
        PageContent c = new PageContent(
                publication.getId(),
                0L,
                new ArrayList<>(Collections.singletonList(0L)),
                new ArrayList<>(Collections.singletonList(2L)));

        when(pageRepository.findOne(0L)).thenReturn(p);
        when(articleRepository.findAll(anyIterable())).thenReturn(articles);
        when(advertRepository.findAll(anyIterable())).thenReturn(adverts);

        pageService.setContent(0L, c);

        verify(pageRepository, times(1)).saveAndFlush(p);
        verify(messagingService, times(1)).broadcastEntityAction(c, Action.UPDATE);
        assertEquals(articles.size(), p.getArticles().size());
        assertEquals(adverts.size(), p.getAdverts().size());
    }

    @Test
    public void delete() {
        Page p = Page.builder().publication(publication).build();
        p.setId(0L);
        List<Long> pageOrder = new ArrayList<>(Collections.singletonList(0L));

        PageService pageServiceSpy = spy(pageService);

        when(pageRepository.findOne(0L)).thenReturn(p);
        when(pageRepository.getPageOrderByPublicationId(publication.getId())).thenReturn(pageOrder);


        pageServiceSpy.delete(0L);

        verify(pageServiceSpy, times(1)).setPageOrder(pageOrder, 0L);
        verify(pageRepository, times(1)).delete(0L);
        verify(messagingService, times(1)).broadcastEntityAction(p, Action.DELETE);
    }
}