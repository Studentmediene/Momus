package no.dusken.momus.article.revision;

import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import no.dusken.momus.common.AbstractControllerTest;
import no.dusken.momus.article.Article;
import no.dusken.momus.article.ArticleService;
import no.dusken.momus.article.ArticleStatus;
import no.dusken.momus.article.revision.ArticleRevision;
import no.dusken.momus.article.revision.ArticleRevisionRepository;

public class ArticleRevisionControllerTest extends AbstractControllerTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRevisionRepository articleRevisionRepository;

    @Test
    public void testGetArticleRevisions() throws Exception {
        Article article = new Article();
        article.setName("Artikkel");
        article.setContent("Innhold");
        article.setJournalists(new HashSet<>());
        article.setPhotographers(new HashSet<>());
        article = articleService.createArticle(article);

        performGetExpectOk("/api/articles/" + article.getId() + "/revisions");
    }

    @Test
    public void testGetArticleRevisionComparison() throws Exception {
        Article article = Article.builder()
                .name("Artikkel")
                .content("Innhold")
                .journalists(new HashSet<>())
                .photographers(new HashSet<>())
                .build();
        article = articleService.createArticle(article);
        
        article.setStatus(ArticleStatus.DESKING);
        articleService.updateArticleStatus(article.getId(), article);

        article = articleService.updateArticleContent(article.getId(), "Nytt innhold");

        Article updatedArticle = Article.builder()
                .name("Artikkel")
                .status(ArticleStatus.PUBLISHED)
                .build();
        updatedArticle.setId(article.getId());
        articleService.updateArticleStatus(article.getId(), updatedArticle);

        List<ArticleRevision> revisions = articleRevisionRepository.findByArticleIdOrderBySavedDateDesc(article.getId());

        performGetExpectOk("/api/articles/" +
                article.getId() +
                "/revisions/" +
                revisions.get(0).getId() + "/" + revisions.get(1).getId());
    }
}
