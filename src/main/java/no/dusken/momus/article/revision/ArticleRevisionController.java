package no.dusken.momus.article.revision;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.dusken.momus.article.revision.diff.DiffMatchPatch;

@RestController
@RequestMapping("/api/articles/{id}/revisions")
public class ArticleRevisionController {

    private ArticleRevisionService articleRevisionService;
    private ArticleRevisionRepository articleRevisionRepository;

    public ArticleRevisionController(ArticleRevisionService articleRevisionService, ArticleRevisionRepository articleRevisionRepository) {
        this.articleRevisionService = articleRevisionService;
        this.articleRevisionRepository = articleRevisionRepository;
    }
    @GetMapping
    public List<ArticleRevision> getArticleRevisions(@PathVariable Long id) {
        return articleRevisionRepository.findByArticleIdOrderBySavedDateDesc(id);
    }

    @GetMapping("{revId1}/{revId2}")
    public List<DiffMatchPatch.Diff> getRevisionsDiffs(
            @PathVariable Long id, @PathVariable Long revId1, @PathVariable Long revId2) {
        return articleRevisionService.getDiffs(id, revId1, revId2);
    }
}