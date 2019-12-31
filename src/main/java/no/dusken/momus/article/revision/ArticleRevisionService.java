package no.dusken.momus.article.revision;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.article.Article;
import no.dusken.momus.article.revision.diff.DiffMatchPatch;
import no.dusken.momus.article.revision.diff.DiffUtil;
import no.dusken.momus.common.exceptions.RestException;

@Service
@Slf4j
public class ArticleRevisionService {
    private final ArticleRevisionRepository articleRevisionRepository;
    private final DiffUtil diffUtil;

    public ArticleRevisionService(ArticleRevisionRepository articleRevisionRepository, DiffUtil diffUtil) {
        this.articleRevisionRepository = articleRevisionRepository;
        this.diffUtil = diffUtil;
    }

    /**
     * Either returns a new revision or reuses the last one.
     * Should be reused if: Last revision is less than three minutes old and was not for a status change
     */
    public ArticleRevision createRevision(Article article) {

        ZonedDateTime saveDate = ZonedDateTime.now();
        ArticleRevision revision = getLastRevision(article);

        boolean isStatusChanged = 
            revision != null && revision.getStatus() != null &&
            !revision.getStatus().equals(article.getStatus());
        
        boolean canReuseOld = 
            revision != null &&
            !isRevisionTooOld(revision, saveDate) &&
            !revision.isStatusChanged() &&
            !isStatusChanged;
        
        if(!canReuseOld) {
            revision = new ArticleRevision();
        }
        
        revision.setSavedDate(ZonedDateTime.now());
        revision.setStatusChanged(isStatusChanged);
        revision.setContent(article.getContent());
        revision.setArticle(article);
        revision.setStatus(article.getStatus());

        revision = articleRevisionRepository.save(revision);
        log.info("Saved revision for article(id:{}) with id: {}", article.getId(), revision.getId());
        return revision;
    }

    public List<DiffMatchPatch.Diff> getDiffs(Long articleId, Long revision1Id, Long revision2Id) {
        ArticleRevision revision1 = getById(revision1Id);
        ArticleRevision revision2 = getById(revision2Id);

        return diffUtil.getDiffList(revision1, revision2);
    }

    private boolean isRevisionTooOld(ArticleRevision revision, ZonedDateTime date) {
        return Duration.between(revision.getSavedDate(), date).toMinutes() > 3;
    }

    private ArticleRevision getLastRevision(Article article) {
        return articleRevisionRepository.findFirstByArticleIdOrderBySavedDateDesc(article.getId()).orElse(null);
    }

    private ArticleRevision getById(Long id) {
        return articleRevisionRepository.findById(id)
            .orElseThrow(() -> new RestException("No revision found with id " + id + " found", HttpServletResponse.SC_BAD_REQUEST));
    }
}