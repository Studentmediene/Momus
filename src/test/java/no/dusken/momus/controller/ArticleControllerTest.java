package no.dusken.momus.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import no.dusken.momus.model.Article;
import no.dusken.momus.model.ArticleRevision;
import no.dusken.momus.model.ArticleStatus;
import no.dusken.momus.service.ArticleService;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.ArticleRevisionRepository;
import no.dusken.momus.service.repository.ArticleStatusRepository;
import no.dusken.momus.service.search.ArticleSearchParams;
import no.dusken.momus.util.TestUtil;

public class ArticleControllerTest extends AbstractControllerTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleStatusRepository articleStatusRepository;

    @Autowired
    private ArticleRevisionRepository articleRevisionRepository;

    @Test
    public void getNonexistentArticle() throws Exception {
        mockMvc.perform(get("/api/article/1")).andExpect(status().isNotFound());
    }

    @Test
    public void testCreateArticle() throws Exception {
        Article article = new Article();
        article.setName("Artikkel");
        mockMvc.perform(buildPost("/api/article", TestUtil.toJsonString(article))).andExpect(status().isOk());

        List<Article> articles = articleRepository.findAll();

        assert articles.size() == 1;
        mockMvc.perform(buildGet("/api/article/" + articles.get(0).getId())).andExpect(status().isOk());
    }

    @Test
    public void testUpdateArticleMetadata() throws Exception {
        ArticleStatus status = articleStatusRepository.saveAndFlush(ArticleStatus.builder().name("Ukjent").build());
        Article article = new Article();
        article.setName("Artikkel");
        article.setStatus(status);
        article = articleRepository.saveAndFlush(article);
        article.setName("Yeah");

        performPatchExpectOk("/api/article/" +  article.getId() + "/metadata", TestUtil.toJsonString(article));
    }

    @Test
    public void testGetArticleContent() throws Exception {
        Article article = new Article();
        article.setName("Artikkel");
        article.setContent("Innhold");
        articleRepository.saveAndFlush(article);

        String content = performGetExpectOk("/api/article/" + article.getId() + "/content")
                .andReturn().getResponse().getContentAsString();

        assert content.equals("\"Innhold\"");
    }

    @Test
    public void testUpdateArticleNote() throws Exception {
        Article article = new Article();
        article.setName("Artikkel");
        articleRepository.saveAndFlush(article);

        performPatchExpectOk("/api/article/" + article.getId() + "/note", "\"Notat\"");
    }

    @Test
    public void testUpdateArticleArchived() throws Exception {
        Article article = new Article();
        article.setName("Artikkel");
        articleRepository.saveAndFlush(article);

        performPatchExpectOk("/api/article/" + article.getId() + "/archived?archived=true", "");
    }

    @Test
    public void testGetIndesignFile() throws Exception {
        Article article = new Article();
        article.setName("Artikkel");
        article.setContent("Innhold");
        article.setJournalists(new HashSet<>());
        article.setPhotographers(new HashSet<>());
        article = articleRepository.saveAndFlush(article);

        MockHttpServletResponse response = performGetExpectOk("/api/article/" + article.getId() + "/indesignfile")
                .andReturn().getResponse();

        assert response.getHeader("Content-Disposition").equals("attachment; filename=\"" + article.getName() + ".txt\"");
        assert response.getHeader("Content-Type").equals("text/plain;charset=UTF-16LE");
    }

    @Test
    public void testGetArticleRevisions() throws Exception {
        Article article = new Article();
        article.setName("Artikkel");
        article.setContent("Innhold");
        article.setJournalists(new HashSet<>());
        article.setPhotographers(new HashSet<>());
        article = articleRepository.saveAndFlush(article);

        performGetExpectOk("/api/article/" + article.getId() + "/revisions");
    }

    @Test
    public void testGetArticleRevisionComparison() throws Exception {
        ArticleStatus status1 = articleStatusRepository.saveAndFlush(ArticleStatus.builder().name("Ukjent").build());
        ArticleStatus status2 = articleStatusRepository.saveAndFlush(ArticleStatus.builder().name("Planlagt").build());
        Article article = Article.builder()
                .name("Artikkel")
                .content("Innhold")
                .journalists(new HashSet<>())
                .photographers(new HashSet<>())
                .build();
        article = articleService.createArticle(article);
        
        article.setStatus(status1);
        articleService.updateArticleStatus(article.getId(), article);

        article = articleService.updateArticleContent(article.getId(), "Nytt innhold");

        Article updatedArticle = Article.builder()
                .name("Artikkel")
                .status(status2)
                .build();
        updatedArticle.setId(article.getId());
        articleService.updateArticleStatus(article.getId(), updatedArticle);

        List<ArticleRevision> revisions = articleRevisionRepository.findByArticleIdOrderBySavedDateDesc(article.getId());

        performGetExpectOk("/api/article/" +
                article.getId() +
                "/revisions/" +
                revisions.get(0).getId() + "/" + revisions.get(1).getId());
    }

    @Test
    public void testGetMultipleArticles() throws Exception {
        Article article1 = new Article();
        article1.setName("Art1");

        Article article2 = new Article();
        article2.setName("Art2");

        article1 = articleService.createArticle(article1);
        article2 = articleService.createArticle(article2);

        performGetExpectOk("/api/article/multiple?ids=" + article1.getId() + "&ids=" + article2.getId());
    }

    @Test
    public void testSearchForArticles() throws Exception {
        Article article = new Article();
        article.setName("Artikkel");

        articleService.createArticle(article);
        ArticleSearchParams params = new ArticleSearchParams(
                "Artikkel",
                null,
                null,
                null,
                null,
                null,
                10,
                1,
                true);
        performPostExpectOk("/api/article/search", TestUtil.toJsonString(params));
    }
}
