package no.dusken.momus.service;

import no.dusken.momus.dto.PageOrder;
import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.LayoutStatus;
import no.dusken.momus.model.Page;
import no.dusken.momus.dto.PageContent;
import no.dusken.momus.dto.PageId;
import no.dusken.momus.model.Publication;
import no.dusken.momus.model.websocket.Action;
import no.dusken.momus.service.repository.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class PageService {

    private final PageRepository pageRepository;
    private final LayoutStatusRepository layoutStatusRepository;
    private final ArticleRepository articleRepository;
    private final AdvertRepository advertRepository;

    private final MessagingService messagingService;

    public PageService(
            PageRepository pageRepository,
            LayoutStatusRepository layoutStatusRepository,
            ArticleRepository articleRepository,
            AdvertRepository advertRepository,
            MessagingService messagingService
    ) {
        this.pageRepository = pageRepository;
        this.layoutStatusRepository = layoutStatusRepository;
        this.articleRepository = articleRepository;
        this.advertRepository = advertRepository;
        this.messagingService = messagingService;
    }

    public Page getPageById(Long id) {
        return pageRepository.findById(id).orElseThrow(() -> new RestException("Not found", 404));
    }

    public List<Page> getPagesInPublication(Long publicationId) {
        return pageRepository.findByPublicationId(publicationId);
    }

    public PageOrder getPageOrderInPublication(Long publicationId) {
        return new PageOrder(publicationId, pageRepository.getPageOrderByPublicationId(publicationId));
    }

    public List<Page> createEmptyPagesInPublication(Long publicationId, Integer afterPage, Integer numPages) {
        List<PageId> pageOrder = pageRepository.getPageOrderByPublicationId(publicationId);
        List<Page> createdPages = new ArrayList<>();
        Publication publication = new Publication();
        publication.setId(publicationId);
        LayoutStatus layoutStatus = layoutStatusRepository.findByName("Ukjent");

        for (int i = 0; i < numPages; i++) {
            Page newPage = Page.builder()
                    .pageNr(afterPage + i + 1)
                    .publication(publication)
                    .layoutStatus(layoutStatus)
                    .build();
            newPage = pageRepository.save(newPage);
            createdPages.add(newPage);
            messagingService.broadcastEntityAction(newPage, Action.CREATE);

            pageOrder.add(afterPage + i, new PageId(newPage.getId()));
        }
        pageRepository.flush();
        setPageOrder(pageOrder, publicationId);
        return createdPages;
    }

    public Page updateMetadata(Long id, Page page) {
        Page existing = getPageById(id);

        existing.setNote(page.getNote());
        existing.setLayoutStatus(page.getLayoutStatus());
        existing.setDone(page.isDone());

        Page saved = pageRepository.saveAndFlush(existing);
        messagingService.broadcastEntityAction(saved, Action.UPDATE);
        return saved;
    }

    public void setPageOrder(PageOrder pageOrder) {
        setPageOrder(pageOrder.getOrder(), pageOrder.getPublicationId());
    }

    public void setPageOrder(List<PageId> pageOrder, Long publicationId) {
        Integer pageNr = 1;
        for (PageId page : pageOrder) {
            pageRepository.updatePageNr(pageNr++, page.getId());
        }
        messagingService.broadcastEntityAction(new PageOrder(publicationId, pageOrder), Action.UPDATE);
    }

    public void setContent(Long id, PageContent content) {
        Page existing = getPageById(id);
        existing.setArticles(new HashSet<>(articleRepository.findAllById(content.getArticles())));
        existing.setAdverts(new HashSet<>(advertRepository.findAllById(content.getAdverts())));
        pageRepository.saveAndFlush(existing);

        messagingService.broadcastEntityAction(content, Action.UPDATE);
    }

    public void delete(Long id) {
        Page page = pageRepository.findById(id).orElseThrow(() -> new RestException("Not found", 404));
        Long publicationId = page.getPublication().getId();

        List<PageId> order = pageRepository.getPageOrderByPublicationId(publicationId);
        order.remove(new PageId(id));
        setPageOrder(order, publicationId);

        messagingService.broadcastEntityAction(page, Action.DELETE);
        pageRepository.deleteById(id);
    }
}
