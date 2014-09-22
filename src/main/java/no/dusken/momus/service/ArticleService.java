/*
 * Copyright 2014 Studentmediene i Trondheim AS
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

import no.dusken.momus.authentication.UserLoginService;
import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.Article;
import no.dusken.momus.model.ArticleRevision;
import no.dusken.momus.model.Person;
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
    ArticleRepository articleRepository;

    @Autowired
    ArticleRevisionRepository articleRevisionRepository;

    @Autowired
    IndesignGenerator indesignGenerator;

    @Autowired
    private UserLoginService userLoginService;

    @PersistenceContext
    EntityManager entityManager;



    public Article getArticleById(Long id) {
        Article article = articleRepository.findOne(id);
        if (article == null) {
            logger.warn("Article with id {} not found, tried by user {}", id, userLoginService.getId());
            throw new RestException("Article "+id+" not found", 404);
        }
        return article;
    }


    public Article createNewArticle(Article article) {
        Long newID = articleRepository.saveAndFlush(article).getId();
        return articleRepository.findOne(newID);
    }

    public Article saveUpdatedArticle(Article article) {
        article.setLastUpdated(new Date());

        logger.info("Article \"{}\" (id: {}) updated by user {}", article.getName(), article.getId(), userLoginService.getId());

//        if (article.getStatus().getName().equals("Publisert")) {
            // export
//        }

        return articleRepository.saveAndFlush(article);
    }

    public Article saveNewContent(Article article) {
        Article existing = articleRepository.findOne(article.getId());
        String content = article.getContent();

        ArticleRevision revision = new ArticleRevision();
        revision.setContent(content);
        revision.setArticle(existing);
        revision.setAuthor(new Person(userLoginService.getId()));
        revision.setSavedDate(new Date());
        revision.setStatus(existing.getStatus());
        revision = articleRevisionRepository.save(revision);

        logger.info("Saved new revision for article(id:{}) with id: {}, content:\n{}", article.getId(), revision.getId(), content);

        existing.setContent(content);
        return saveUpdatedArticle(existing);
    }

    public Article saveMetadata(Article article) {
        Article existing = articleRepository.findOne(article.getId());

        existing.setName(article.getName());
        existing.setJournalists(article.getJournalists());
        existing.setPhotographers(article.getPhotographers());
        existing.setComment(article.getComment());
        existing.setPublication(article.getPublication());
        existing.setType(article.getType());
        existing.setStatus(article.getStatus());

        return saveUpdatedArticle(existing);
    }


    public Article saveNote(Article article) {
        Article existing = articleRepository.findOne(article.getId());

        existing.setNote(article.getNote());

        return saveUpdatedArticle(existing);
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

        query.setMaxResults(201);
        // TODO add paging of results?

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

}
