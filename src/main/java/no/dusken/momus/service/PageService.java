package no.dusken.momus.service;

import no.dusken.momus.dto.PageOrder;
import no.dusken.momus.model.Page;
import no.dusken.momus.dto.PageContent;
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
    private final PublicationRepository publicationRepository;
    private final LayoutStatusRepository layoutStatusRepository;
    private final ArticleRepository articleRepository;
    private final AdvertRepository advertRepository;

    private final MessagingService messagingService;

    public PageService(
            PageRepository pageRepository,
            PublicationRepository publicationRepository,
            LayoutStatusRepository layoutStatusRepository,
            ArticleRepository articleRepository,
            AdvertRepository advertRepository,
            MessagingService messagingService
    ) {
        this.pageRepository = pageRepository;
        this.publicationRepository = publicationRepository;
        this.layoutStatusRepository = layoutStatusRepository;
        this.articleRepository = articleRepository;
        this.advertRepository = advertRepository;
        this.messagingService = messagingService;
    }

    public void setPageOrder(PageOrder pageOrder) {
        Integer pageNr = 1;
        for (Long pageId : pageOrder.getOrder()) {
            pageRepository.updatePageNr(pageNr++, pageId);
        }
        messagingService.broadcastEntityAction(pageOrder, Action.UPDATE);
    }

    public List<Page> createEmptyPagesInPublication(Long publicationId, Integer afterPage, Integer numPages) {
        PageOrder pageOrder = new PageOrder(publicationId, pageRepository.getPageOrderByPublicationId(publicationId));
        List<Page> createdPages = new ArrayList<>();

        for (int i = 0; i < numPages; i++) {
            Page newPage = Page.builder()
                    .pageNr(afterPage + i + 1)
                    .publication(publicationRepository.findOne(publicationId))
                    .layoutStatus(layoutStatusRepository.findByName("Ukjent"))
                    .build();
            newPage = pageRepository.save(newPage);
            createdPages.add(newPage);

            pageOrder.getOrder().add(afterPage + i, newPage.getId());
        }
        pageRepository.flush();
        setPageOrder(pageOrder);
        return createdPages;
    }

    public Page updateMetadata(Long id, Page page) {
        Page existing = pageRepository.findOne(id);

        existing.setNote(page.getNote());
        existing.setLayoutStatus(page.getLayoutStatus());
        existing.setDone(page.isDone());

        Page saved = pageRepository.saveAndFlush(existing);
        messagingService.broadcastEntityAction(saved, Action.UPDATE);
        return saved;
    }

    public void setContent(Long id, PageContent content) {
        Page existing = pageRepository.findOne(id);
        existing.setArticles(new HashSet<>(articleRepository.findAll(content.getArticles())));
        existing.setAdverts(new HashSet<>(advertRepository.findAll(content.getAdverts())));
        pageRepository.saveAndFlush(existing);

        messagingService.broadcastEntityAction(content, Action.UPDATE);
    }

    public void delete(Long id) {
        Page page = pageRepository.findOne(id);

        List<Long> order = pageRepository.getPageOrderByPublicationId(page.getPublication().getId());
        order.remove(id);
        setPageOrder(new PageOrder(page.getPublication().getId(), order));

        messagingService.broadcastEntityAction(page, Action.DELETE);
        pageRepository.delete(id);
    }
}
