package no.dusken.momus.controller;

import no.dusken.momus.model.Article;
import no.dusken.momus.model.ArticleRevision;
import no.dusken.momus.model.ArticleStatus;
import no.dusken.momus.service.ArticleService;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.ArticleRevisionRepository;
import no.dusken.momus.service.repository.ArticleStatusRepository;
import no.dusken.momus.service.search.ArticleSearchParams;
import no.dusken.momus.util.TestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        mockMvc.perform(get("/article/1")).andExpect(status().isNotFound());
    }

    @Test
    public void testSaveArticle() throws Exception {
        Article article = new Article();
        article.setName("Artikkel");
        mockMvc.perform(buildPost("/article", TestUtil.toJsonString(article))).andExpect(status().isOk());

        List<Article> articles = articleRepository.findAll();

        assert articles.size() == 1;
        mockMvc.perform(buildGet("/article/" + articles.get(0).getId())).andExpect(status().isOk());
    }

    @Test
    public void testUpdateArticleMetadata() throws Exception {
        ArticleStatus status = articleStatusRepository.saveAndFlush(ArticleStatus.builder().name("Ukjent").build());
        Article article = new Article();
        article.setName("Artikkel");
        article.setStatus(status);
        article = articleRepository.saveAndFlush(article);
        article.setName("Yeah");

        performPatchExpectOk("/article/" +  article.getId() + "/metadata", TestUtil.toJsonString(article));
    }

    @Test
    public void testGetArticleContent() throws Exception {
        Article article = new Article();
        article.setName("Artikkel");
        article.setContent("Innhold");
        articleRepository.saveAndFlush(article);

        String content = performGetExpectOk("/article/" + article.getId() + "/content")
                .andReturn().getResponse().getContentAsString();

        assert content.equals("\"Innhold\"");
    }

    @Test
    public void testUpdateArticleNote() throws Exception {
        Article article = new Article();
        article.setName("Artikkel");
        articleRepository.saveAndFlush(article);

        performPatchExpectOk("/article/" + article.getId() + "/note", "\"Notat\"");
    }

    @Test
    public void testUpdateArticleArchived() throws Exception {
        Article article = new Article();
        article.setName("Artikkel");
        articleRepository.saveAndFlush(article);

        performPatchExpectOk("/article/" + article.getId() + "/archived?archived=true", "");
    }

    @Test
    public void testGetIndesignFile() throws Exception {
        Article article = new Article();
        article.setName("Artikkel");
        article.setContent("Innhold");
        article.setJournalists(new HashSet<>());
        article.setPhotographers(new HashSet<>());
        article = articleRepository.saveAndFlush(article);

        MockHttpServletResponse response = performGetExpectOk("/article/" + article.getId() + "/indesignfile")
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

        performGetExpectOk("/article/" + article.getId() + "/revisions");
    }

    @Test
    public void testGetArticleRevisionComparison() throws Exception {
        ArticleStatus status1 = articleStatusRepository.saveAndFlush(ArticleStatus.builder().name("Ukjent").build());
        ArticleStatus status2 = articleStatusRepository.saveAndFlush(ArticleStatus.builder().name("Planlagt").build());
        Article article = new Article();
        article.setName("Artikkel");
        article.setContent("Innhold");
        article.setStatus(status1);
        article.setJournalists(new HashSet<>());
        article.setPhotographers(new HashSet<>());
        article = articleService.saveArticle(article);
        Article updatedArticle = Article.builder().id(article.getId()).build();
        updatedArticle.setContent("Nytt innhold");
        article = articleService.updateArticleContent(updatedArticle);
        updatedArticle.setName("Artikkel");
        updatedArticle.setStatus(status2);
        articleService.updateArticleMetadata(article.getId(), updatedArticle);

        List<ArticleRevision> revisions = articleRevisionRepository.findByArticleIdOrderBySavedDateDesc(article.getId());

        performGetExpectOk("/article/" +
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

        article1 = articleService.saveArticle(article1);
        article2 = articleService.saveArticle(article2);

        performGetExpectOk("/article/multiple?ids=" + article1.getId() + "&ids=" + article2.getId());
    }

    @Test
    public void testSearchForArticles() throws Exception {
        Article article = new Article();
        article.setName("Artikkel");

        articleService.saveArticle(article);
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
        performPostExpectOk("/article/search", TestUtil.toJsonString(params));
    }
}
