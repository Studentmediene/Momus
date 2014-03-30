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

import no.dusken.momus.model.Article;
import no.dusken.momus.model.Updates;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ArticleService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleRevisionRepository articleRevisionRepository;

    @PersistenceContext
    EntityManager entityManager;



    public Article getArticleById(Long id) {
        return articleRepository.findOne(id);
    }

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Article saveNewArticle(Article article) {
        Long newID = articleRepository.saveAndFlush(article).getId();
        return articleRepository.findOne(newID);
    }

    public Article saveUpdatedArticle(Article article) {
        articleRepository.saveAndFlush(article);
        return articleRepository.findOne(article.getId());
    }

    public Article updateArticle(Updates<Article> updates) {
        Article articleFromClient = updates.getObject();
        Article articleFromServer = getArticleById(articleFromClient.getId());

        Set<String> updatedFields = updates.getUpdatedFields();
        if (updatedFields.contains("content")) {
            articleFromServer.setContent(articleFromClient.getContent());
        }
        if (updatedFields.contains("note")) {
            articleFromServer.setNote(articleFromClient.getNote());
        }
        if (updatedFields.contains("name")) {
            articleFromServer.setName(articleFromClient.getName());
        }
        if (updatedFields.contains("journalists")) {
            articleFromServer.setJournalists(articleFromClient.getJournalists());
        }
        if (updatedFields.contains("photographers")) {
            articleFromServer.setPhotographers(articleFromClient.getPhotographers());
        }
        if (updatedFields.contains("publication")) {
            articleFromServer.setPublication(articleFromClient.getPublication());
        }

        return saveUpdatedArticle(articleFromServer);
    }

    public List<Article> searchForArticles(ArticleSearchParams params) {
        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);
        String queryText = builder.getFullQuery();
        Map<String, Object> queryParams = builder.getQueryParams();

        TypedQuery<Article> query = entityManager.createQuery(queryText, Article.class);

        for (Map.Entry<String, Object> e : queryParams.entrySet()) {
            query.setParameter(e.getKey(), e.getValue());
        }

        query.setMaxResults(100);
        // TODO add paging of results?

        return query.getResultList();
    }
}
