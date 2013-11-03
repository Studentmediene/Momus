/*
 * Copyright 2013 Studentmediene i Trondheim AS
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
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.ArticleRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static no.dusken.momus.service.specification.ArticleSpecifications.*;
import static org.springframework.data.jpa.domain.Specifications.where;

@Service
public class ArticleService {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleRevisionRepository articleRevisionRepository;

    public Article getArticleById(Long id) {
        return articleRepository.findOne(id);
    }

    public Article saveArticle(Article article) {
        Long newID = articleRepository.saveAndFlush(article).getId();
        return articleRepository.findOne(newID);
    }

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public List<Article> search(String content, String name) {
        Specifications<Article> criteria = where(starter());

        if (!content.equals("")) {
            criteria = criteria.and(contentContains(content));
        }

        if (!name.equals("")) {
            criteria = criteria.and(nameIs(name));
        }

        criteria = criteria.and(photoIdIs(1L));

        return articleRepository.findAll(criteria);
    }

    public Article saveEntireArticle(Article article) {
        articleRepository.saveAndFlush(article);
        return articleRepository.findOne(article.getId());
    }

    public String saveContent(String content, Long id) {
        Article article = articleRepository.findOne(id);
        article.setContent(content);
        articleRepository.saveAndFlush(article);
        return article.getContent();
    }

    public String saveName(String name, Long id) {
        Article article = articleRepository.findOne(id);
        article.setName(name);
        articleRepository.saveAndFlush(article);
        return article.getName();
    }

    public String saveNote(String note, Long id) {
        Article article = articleRepository.findOne(id);
        article.setNote(note);
        articleRepository.saveAndFlush(article);
        return article.getNote();
    }

    public Set<Person> saveJournalists(Set<Person> persons, Long id) {
        Article article = articleRepository.findOne(id);
        article.setJournalists(persons);
        articleRepository.saveAndFlush(article);
        return article.getJournalists();
    }

    public Set<Person> savePhotographers(Set<Person> persons, Long id) {
        Article article = articleRepository.findOne(id);
        article.setPhotographers(persons);
        articleRepository.saveAndFlush(article);
        return article.getPhotographers();
    }
}
