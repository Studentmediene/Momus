package no.dusken.momus.controller;


import no.dusken.momus.model.Article;
import no.dusken.momus.model.ArticleStatus;
import no.dusken.momus.model.Person;
import no.dusken.momus.model.Publication;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    private ArticleController articleController;
    private PersonController personController;
    private PublicationController publicationController;


    public List<Publication> getPublicationsList(int year) {
        return publicationController.getAllPublications(year);
    }

    public List<Person> getPersonsList(List<Person> personsList) {
        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < personsList.size(); i++ ) {
           persons.add(personController.getPersonById(personsList.get(i).getId()));
        }
        return persons;
    }

    public List<Article> getArticlesList(List<Article> articleList) {
        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            articles.add(articleController.getArticleByID(articleList.get(i).getId()));
        }
        return articles;
    }

    public List<ArticleStatus> getArticleStatusList(List<ArticleStatus> articleStatusList) {
        return articleStatusList;
    }



}
