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

import com.google.api.services.drive.model.File;
import no.dusken.momus.authentication.UserDetailsService;
import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.Article;
import no.dusken.momus.model.ArticleRevision;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.drive.GoogleDriveService;
import no.dusken.momus.service.indesign.IndesignExport;
import no.dusken.momus.service.indesign.IndesignGenerator;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.ArticleRevisionRepository;
import no.dusken.momus.service.search.ArticleQueryBuilder;
import no.dusken.momus.service.search.ArticleSearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ArticleService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleRevisionRepository articleRevisionRepository;

    @Autowired
    private IndesignGenerator indesignGenerator;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private GoogleDriveService googleDriveService;

    @PersistenceContext
    private EntityManager entityManager;


    public Article getArticleById(Long id) {
        Article article = articleRepository.findOne(id);
        if (article == null) {
            logger.warn("Article with id {} not found, tried by user {}", id, userDetailsService.getLoggedInPerson().getId());
            throw new RestException("Article " + id + " not found", 404);
        }
        return article;
    }

    public Article saveArticle(Article article) {
        File document = googleDriveService.createDocument(article.getName());

        if (document == null) {
            throw new RestException("Couldn't create article, Google Docs failed", 500);
        }

        article.setGoogleDriveId(document.getId());

        Article newArticle = articleRepository.saveAndFlush(article);

        logger.info("Article with id {} created with data: {}", newArticle.getId(), newArticle.dump());
        return newArticle;
    }

    public Article updateArticle(Article article) {
        article.setLastUpdated(new Date());
        logger.info("Article with id {} updated, data: {}", article.getId(), article.dump());
        return articleRepository.saveAndFlush(article);
    }

    public Article updateArticleContent(Article article) {
        Article existing = articleRepository.findOne(article.getId());
        String newContent = article.getContent();
        String oldContent = existing.getContent();

        if (newContent.equals(oldContent)) {
            // Inserting comments in the Google Docs triggers a change, but the content we see is the same.
            // So it would look weird having multiple revisions without any changes.
            logger.info("No changes made to content of article with id {}, not updating it", article.getId());
            return existing;
        }

        existing.setContent(newContent);

        createNewRevision(existing, false);

        return updateArticle(existing);
    }

    public Article updateArticleMetadata(Article article) {
        Article existing = articleRepository.findOne(article.getId());
        if (!article.getStatus().equals(existing.getStatus())) {
            createNewRevision(article, true);
        }

        existing.setName(article.getName());
        existing.setJournalists(article.getJournalists());
        existing.setPhotographers(article.getPhotographers());
        existing.setComment(article.getComment());
        existing.setPublication(article.getPublication());
        existing.setType(article.getType());
        existing.setStatus(article.getStatus());
        existing.setSection(article.getSection());
        existing.setUseIllustration(article.getUseIllustration());
        existing.setImageText(article.getImageText());
        existing.setQuoteCheckStatus(article.getQuoteCheckStatus());
        existing.setExternalAuthor(article.getExternalAuthor());
        existing.setExternalPhotographer(article.getExternalPhotographer());
        existing.setPhotoStatus(article.getPhotoStatus());
        existing.setReview(article.getReview());

        return updateArticle(existing);
    }

    public Article updateNote(Article article, String note) {
        article.setNote(note);
        return updateArticle(article);
    }

    public Article updateArchived(Article article, Boolean archived) {
        article.setArchived(archived);
        return updateArticle(article);
    }

    public List<Article> searchForArticles(ArticleSearchParams params) {
        long start = System.currentTimeMillis();

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);
        String queryText = builder.getFullQuery();
        Map<String, Object> queryParams = builder.getQueryParams();

        TypedQuery<Article> query = entityManager.createQuery(queryText, Article.class);

        for (Map.Entry<String, Object> e : queryParams.entrySet()) {
            query.setParameter(e.getKey(), e.getValue());
        }

        query.setMaxResults(params.getPageSize() + 1); // One extra, so searchpage can see if there is "more pages"
        query.setFirstResult(params.getStartOfPage());

        List<Article> resultList = query.getResultList();

        long end = System.currentTimeMillis();
        long timeUsed = end - start;
        logger.debug("Time spent on search: {}ms", timeUsed);
        if (timeUsed > 800) {
            logger.warn("Time spent on search high ({}ms), params were: {}", timeUsed, params);
        }

        return resultList;
    }

    public IndesignExport exportArticle(Long id) {
        Article article = getArticleById(id);
        return indesignGenerator.generateFromArticle(article);
    }

    public ArticleRepository getArticleRepository() {
        return articleRepository;
    }


    private void createNewRevision(Article article, boolean changedStatus) {
        ArticleRevision revision = getExistingRevision(article, changedStatus);


        revision.setStatusChanged(changedStatus);
        revision.setContent(article.getContent());
        revision.setArticle(article);
        revision.setStatus(article.getStatus());

        revision = articleRevisionRepository.save(revision);
        logger.info("Saved revision for article(id:{}) with id: {}", article.getId(), revision.getId());
    }

    /**
     * Returns a reusable revision if one is found, or a new one otherwise.
     * This is to not get too man small revision.
     * If the revision is for a status change, a new one is returned anyway
     * A reusable revision is one that wasn't for a status change and not too old
     */
    private ArticleRevision getExistingRevision(Article article, boolean changedStatus) {
        ArticleRevision newRevision = new ArticleRevision();
        newRevision.setSavedDate(new Date());

        if (changedStatus) {
            return newRevision;
        }

        List<ArticleRevision> revisions = articleRevisionRepository.findByArticleIdOrderBySavedDateDesc(article.getId());

        if (revisions.size() == 0) {
            return newRevision;
        }

        ArticleRevision existing = revisions.get(0);
        long timeDiff = new Date().getTime() - existing.getSavedDate().getTime();

        if (timeDiff < 3 * 60 * 1000 && !existing.isStatusChanged()) {
            logger.info("Reusing revision {} for article {}", existing.getId(), article.getId());
            return existing;
        } else {
            return newRevision;
        }
    }

    public static String createRawContent(Article article){
        return ArticleService.stripOffHtml(article.getContent()).toLowerCase();
    }

    private static String stripOffHtml(String html){
        String[] tags = {"<h1>","<h2>","<h3>","<h4>","<h5>","<p>","<i>","<b>", "<blockquote>","<br>","<ul>","<ol>","<li>"};
        for (String tag : tags) {
            html = html.replaceAll(tag," ").replaceAll(tag.substring(0,1)+"/"+tag.substring(1,tag.length()),"");
        }

        // Remove consecutive spaces
        html = html.replaceAll("\\s+", " ");

        return html;
    }

}
