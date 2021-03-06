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

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;

import com.google.api.services.drive.model.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.dto.ArticleContent;
import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.Article;
import no.dusken.momus.model.ArticleRevision;
import no.dusken.momus.model.ArticleStatus;
import no.dusken.momus.model.Person;
import no.dusken.momus.model.websocket.Action;
import no.dusken.momus.service.drive.GoogleDriveService;
import no.dusken.momus.service.indesign.IndesignExport;
import no.dusken.momus.service.indesign.IndesignGenerator;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.ArticleReviewRepository;
import no.dusken.momus.service.repository.ArticleRevisionRepository;
import no.dusken.momus.service.repository.ArticleStatusRepository;
import no.dusken.momus.service.repository.PersonRepository;
import no.dusken.momus.service.search.ArticleQuery;
import no.dusken.momus.service.search.ArticleQueryBuilder;
import no.dusken.momus.service.search.ArticleSearchParams;

@Service
@Slf4j
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleRevisionRepository articleRevisionRepository;
    private final ArticleStatusRepository articleStatusRepository;
    private final ArticleReviewRepository articleReviewRepository;
    private final PersonRepository personRepository;
    private final ArticleQueryBuilder articleQueryBuilder;
    private final GoogleDriveService googleDriveService;
    private final MessagingService messagingService;
    private final IndesignGenerator indesignGenerator;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${drive.syncEnabled}")
    private boolean driveEnabled;

    public ArticleService(ArticleRepository articleRepository, ArticleRevisionRepository articleRevisionRepository,
        ArticleStatusRepository articleStatusRepository,
        ArticleReviewRepository articleReviewRepository,
        PersonRepository personRepository,
        ArticleQueryBuilder articleQueryBuilder,
        GoogleDriveService googleDriveService,
        MessagingService messagingService,
        IndesignGenerator indesignGenerator
    ) {
        this.articleRepository = articleRepository;
        this.articleRevisionRepository = articleRevisionRepository;
        this.articleStatusRepository = articleStatusRepository;
        this.articleReviewRepository = articleReviewRepository;
        this.personRepository = personRepository;
        this.articleQueryBuilder = articleQueryBuilder;
        this.googleDriveService = googleDriveService;
        this.messagingService = messagingService;
        this.indesignGenerator = indesignGenerator;
    }

    public Article getArticleById(Long id) {
        return articleRepository.findById(id)
            .orElseThrow(() -> new RestException("Article with id="+id+" not found", HttpServletResponse.SC_NOT_FOUND));
    }

    public List<Article> getArticlesByIds(List<Long> ids) {
        return articleRepository.findAllById(ids);
    }

    public List<Article> getArticlesInPublication(Long publicationId) {
        return articleRepository.findByPublicationId(publicationId);
    }

    public List<Article> getLastArticlesForUser(Long userId) {
        Person user = personRepository.findById(userId)
            .orElseThrow(() -> new RestException("User not found", HttpServletResponse.SC_NOT_FOUND));
        
        return articleRepository.findByJournalistsOrPhotographersOrGraphicsContains(user, new PageRequest(0, 10));
    }

    public ArticleContent getArticleContent(Long id) {
        return articleRepository.findArticleContentById(id)
            .orElseThrow(() -> new RestException("Article with id="+id+" not found", HttpServletResponse.SC_NOT_FOUND));
    }

    public Article createArticle(Article article) {
        if(driveEnabled) {
            File document = googleDriveService.createDocument(article.getName());

            if (document == null) {
                throw new RestException("Couldn't create article, Google Docs failed", 500);
            }

            article.setGoogleDriveId(document.getId());
        }

        article.setStatus(articleStatusRepository.findById(2L).orElse(null));
        article.setReview(articleReviewRepository.findById(1L).orElse(null));

        Article newArticle = articleRepository.saveAndFlush(article);
        log.info("Article with id {} created with data: {}", newArticle.getId(), newArticle);

        messagingService.broadcastEntityAction(article, Action.CREATE);
        return newArticle;
    }

    public Article updateArticle(Article article) {
        article.setLastUpdated(ZonedDateTime.now());
        log.info("Article with id {} updated, data: {}", article.getId(), article);

        messagingService.broadcastEntityAction(article, Action.UPDATE);
        return articleRepository.saveAndFlush(article);
    }

    public Article updateArticleContent(Long id, String newContent) {
        Article existing = getArticleById(id);
        String oldContent = existing.getContent();

        if (newContent.equals(oldContent)) {
            // Inserting comments in the Google Docs triggers a change, but the content we see is the same.
            // So it would look weird having multiple revisions without any changes.
            log.info("No changes made to content of article with id {}, not updating it", id);
            return existing;
        }

        existing.setContent(newContent);

        createRevision(existing);

        return updateArticle(existing);
    }

    public Article updateArticleMetadata(Long id, Article article) {
        Article existing = getArticleById(id);

        ArticleStatus oldStatus = existing.getStatus();

        existing.setName(article.getName());
        existing.setJournalists(article.getJournalists());
        existing.setPhotographers(article.getPhotographers());
        existing.setGraphics(article.getGraphics());
        existing.setComment(article.getComment());
        existing.setPublication(article.getPublication());
        existing.setType(article.getType());
        existing.setStatus(article.getStatus());
        existing.setSection(article.getSection());
        existing.setUseIllustration(article.isUseIllustration());
        existing.setImageText(article.getImageText());
        existing.setQuoteCheckStatus(article.isQuoteCheckStatus());
        existing.setExternalAuthor(article.getExternalAuthor());
        existing.setExternalPhotographer(article.getExternalPhotographer());
        existing.setPhotoStatus(article.getPhotoStatus());
        existing.setReview(article.getReview());

        if (!article.getStatus().equals(oldStatus)) {
            createRevision(existing);
        }

        return updateArticle(existing);
    }

    public Article updateArticleStatus(Long id, Article article) {
        Article existing = getArticleById(id);
        ArticleStatus oldStatus = existing.getStatus();

        existing.setStatus(article.getStatus());
        existing.setComment(article.getComment());
        existing.setPhotoStatus(article.getPhotoStatus());
        existing.setReview(article.getReview());

        if (!article.getStatus().equals(oldStatus)) {
            createRevision(existing);
        }

        return updateArticle(existing);
    }

    public Article updateNote(Long id, String note) {
        Article article = getArticleById(id);
        article.setNote(note);
        return updateArticle(article);
    }

    public Article updateArchived(Long id, Boolean archived) {
        Article article = getArticleById(id);
        article.setArchived(archived);
        return updateArticle(article);
    }

    public List<Article> searchForArticles(ArticleSearchParams params) {
        long start = System.currentTimeMillis();

        ArticleQuery articleQuery = articleQueryBuilder.buildQuery(params);

        TypedQuery<Article> query = entityManager.createQuery(articleQuery.getQuery(), Article.class);

        for (Map.Entry<String, Object> e : articleQuery.getParams().entrySet()) {
            query.setParameter(e.getKey(), e.getValue());
        }

        query.setMaxResults(params.getPageSize() + 1); // One extra, so searchpage can see if there is "more pages"
        query.setFirstResult(params.getStartOfPage());

        List<Article> resultList = query.getResultList();

        long end = System.currentTimeMillis();
        long timeUsed = end - start;
        log.debug("Time spent on search: {}ms", timeUsed);
        if (timeUsed > 800) {
            log.warn("Time spent on search high ({}ms), params were: {}", timeUsed, params);
        }

        return resultList;
    }

    public IndesignExport exportArticle(Long id) {
        return indesignGenerator.generateFromArticle(getArticleById(id));
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

    /**
     * Will get latest changes from google drive and update article content
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void sync() {
        Set<String> modified = googleDriveService.findModifiedFileIds();
        articleRepository.findByGoogleDriveIdIn(modified).forEach(article -> {
            String newContent = googleDriveService.getContentFromDrive(article);
            updateArticleContent(article.getId(), newContent);
        });
        log.debug("Done syncing, updated {} articles", modified.size());
    }

    private boolean isRevisionTooOld(ArticleRevision revision, ZonedDateTime date) {
        return Duration.between(revision.getSavedDate(), date).toMinutes() > 3;
    }

    private ArticleRevision getLastRevision(Article article) {
        return articleRevisionRepository.findFirstByArticleIdOrderBySavedDateDesc(article.getId()).orElse(null);
    }

    public static String createRawContent(Article article){
        return ArticleService.stripOffHtml(article.getContent()).toLowerCase();
    }

    private static String stripOffHtml(String html){
        String[] tags = {"<h1>","<h2>","<h3>","<h4>","<h5>","<p>","<i>","<b>", "<blockquote>","<br>","<ul>","<ol>","<li>"};
        for (String tag : tags) {
            html = html.replaceAll(tag," ").replaceAll(tag.substring(0,1)+"/"+tag.substring(1,tag.length()),"");
        }

        // Remove consecutive spaces and trim
        html = html.replaceAll("\\s+", " ").trim();

        return html;
    }
}
