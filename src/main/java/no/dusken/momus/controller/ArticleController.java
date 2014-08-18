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

package no.dusken.momus.controller;

import no.dusken.momus.model.Article;
import no.dusken.momus.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody Article getArticleByID(@PathVariable("id") Long id) {
        return articleService.getArticleById(id);
    }

    @RequestMapping(value = "/publication/{id}", method = RequestMethod.GET)
    public @ResponseBody List<Article> getAllArticlesByPublicationID(@PathVariable("id") Long id) {
        return articleService.getArticleRepository().findByPublicationId(id);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public @ResponseBody Article saveArticleContents(@RequestBody Article article){
        return articleService.saveUpdatedArticle(article);
    }

    @RequestMapping(value = "/metadata", method = RequestMethod.PUT)
    public @ResponseBody Article updateArticleMetadata(@RequestBody Article article){
        return articleService.saveMetadata(article);
    }

    @RequestMapping(value = "/content", method = RequestMethod.PUT)
    public @ResponseBody Article updateArticleContentText(@RequestBody Article article){
        return articleService.saveNewContent(article);
    }

    @RequestMapping(value = "/note", method = RequestMethod.PUT)
    public @ResponseBody Article updateArticleNote(@RequestBody Article article){
        return articleService.saveNote(article);
    }
}
