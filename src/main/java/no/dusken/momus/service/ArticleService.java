package no.dusken.momus.service;

import no.dusken.momus.model.Article;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.ArticleRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

import static no.dusken.momus.service.specification.ArticleSpecifications.*;
import static org.springframework.data.jpa.domain.Specifications.*;

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

    public void saveArticleContents(Article article) {
        Article oldArticle = articleRepository.findOne(article.getId());
    }

}
