package no.dusken.momus.publication.page;

import no.dusken.momus.advert.Advert;
import no.dusken.momus.advert.AdvertService;
import no.dusken.momus.article.Article;
import no.dusken.momus.article.ArticleService;
import no.dusken.momus.common.AbstractControllerTest;
import no.dusken.momus.publication.LayoutStatus;
import no.dusken.momus.publication.LayoutStatusRepository;
import no.dusken.momus.publication.Publication;
import no.dusken.momus.publication.PublicationService;
import no.dusken.momus.util.TestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class PageControllerTest extends AbstractControllerTest {

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private LayoutStatusRepository layoutStatusRepository;

    private Publication publication;

    @Override
    protected void internalSetup() {
        publication = publicationService.createPublication(Publication.builder().name("UD").releaseDate(LocalDate.now()).build(), 10);
    }

    @Test
    public void getByPublicationId() throws Exception {
        performGetExpectOk("/api/pages?publicationId=" + publication.getId())
                .andExpect(jsonPath("$.length()", is(10)));
    }

    @Test
    public void getPageOrderByPublicationId() throws Exception {
        performGetExpectOk("/api/pages/page-order?publicationId=" + publication.getId())
                .andExpect(jsonPath("$.order.length()", is(10)));
    }

    @Test
    public void setPageOrder() throws Exception {
        List<PageId> pageOrder = pageRepository.getPageOrderByPublicationId(publication.getId());
        Collections.shuffle(pageOrder);

        performPutExpectOk("/api/pages/page-order", TestUtil.toJsonString(new PageOrder(publication.getId(), pageOrder)));

        List<PageId> updatedPageOrder = pageRepository.getPageOrderByPublicationId(publication.getId());

        assert updatedPageOrder.equals(pageOrder);
    }

    @Test
    public void createEmptyPagesInPublication() throws Exception {
        performPostExpectOk("/api/pages/empty?publicationId=" + publication.getId() + "&afterPage=0&numNewPages=2", "")
                .andExpect(jsonPath("$.length()", is(2)));

        assert pageRepository.findByPublicationId(publication.getId()).size() == 12;
    }

    @Test
    public void setContent() throws Exception {
        Article article = articleService.createArticle(Article.builder().name("Artikkel").build());
        Advert advert = advertService.createAdvert(Advert.builder().name("Ad").build());
        Page page = pageRepository.findByPublicationId(publication.getId()).get(0);

        performPutExpectOk(
                "/api/pages/" + page.getId() + "/content",
                TestUtil.toJsonString(new PageContent(
                        publication.getId(),
                        page.getId(),
                        Collections.singletonList(article.getId()),
                        Collections.singletonList(advert.getId()))));

        page = pageRepository.findById(page.getId()).get();
        assert page.getArticles().size() == 1;
        assert page.getAdverts().size() == 1;
    }

    @Test
    public void updateMetadata() throws Exception {
        Page page = pageRepository.findByPublicationId(publication.getId()).get(0);

        LayoutStatus s = layoutStatusRepository.findAll().get(1);
        page.setLayoutStatus(s);
        page.setDone(true);

        performPatchExpectOk("/api/pages/" + page.getId() + "/metadata", TestUtil.toJsonString(page));

        page = pageRepository.findById(page.getId()).get();

        assertEquals(true, page.isDone());
        assertEquals(s, page.getLayoutStatus());
    }

    @Test
    public void deletePage() throws Exception {
        Page page = pageRepository.findByPublicationId(publication.getId()).get(0);

        performDeleteExpectOk("/api/pages/" + page.getId());

        List<Page> pages = pageRepository.findByPublicationId(publication.getId());

        assert !pages.contains(page);
    }
}
