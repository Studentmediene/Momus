package no.dusken.momus.controller;

import no.dusken.momus.model.Article;
import no.dusken.momus.model.ArticleType;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    ArticleService articleService;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Article> yo() {
        return articleService.getAllArticles();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/create")
    public @ResponseBody Article create() {
        Article newArticle = new Article();
        newArticle.setContent("Test 444");
        newArticle.setName("Name2");
        newArticle.setNote("Note");
        Set<Person> photographers = new HashSet<>();
        photographers.add(new Person(1L));
        newArticle.setPhotographers(photographers);
        newArticle.setLastUpdated(new Date());

        return articleService.saveArticle(newArticle);
    }

    @RequestMapping("/search/{name}/{content}")
    public @ResponseBody List<Article> getFromSearch(@PathVariable("name") String name, @PathVariable("content") String content) {
        return articleService.search(content, name);
    }

}
