package no.dusken.momus.controller;

import no.dusken.momus.model.Article;
import no.dusken.momus.model.ArticleType;
import no.dusken.momus.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

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
        newArticle.setContent("Og til slutt så var det meg og du er rar!");
        newArticle.setName("Name");
        newArticle.setNote("Note");
        newArticle.setLastUpdated(new Date());

        return articleService.saveArticle(newArticle);
    }

    @RequestMapping("/search/{name}/{content}")
    public @ResponseBody List<Article> getFromSearch(@PathVariable("name") String name, @PathVariable("content") String content) {
        return articleService.search(content, name);
    }

}
