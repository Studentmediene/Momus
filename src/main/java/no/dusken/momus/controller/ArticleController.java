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

package no.dusken.momus.controller;

import no.dusken.momus.model.Article;
import no.dusken.momus.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * TODO: Add pagination or something, getting all articles will possibly be MANY
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }

    @RequestMapping(value = "/article/{id}", method = RequestMethod.GET)
    public @ResponseBody Article getArticleByID(@PathVariable("id") Long id) {
        return articleService.getArticleById(id);
    }

    /**
     * TODO: Add full search
     */
    @RequestMapping("/search/{name}/{content}")
    public @ResponseBody List<Article> getFromSearch(@PathVariable("name") String name, @PathVariable("content") String content) {
        return articleService.search(content, name);
    }

}
