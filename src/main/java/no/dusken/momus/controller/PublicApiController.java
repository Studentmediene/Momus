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

import no.dusken.momus.model.*;
import no.dusken.momus.service.ArticleService;
import no.dusken.momus.service.PublicationService;
import no.dusken.momus.service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin(methods = {RequestMethod.GET})
@Controller
@RequestMapping("/public")
public class PublicApiController {

    @Autowired
    ArticleService articleService;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleStatusRepository articleStatusRepository;

    @Autowired
    ArticleReviewRepository articleReviewRepository;

    @Autowired
    PublicationRepository publicationRepository;

    @Autowired
    PublicationService publicationService;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    LayoutStatusRepository layoutStatusRepository;

    @RequestMapping("/test")
    public @ResponseBody String testMe() {
        return "You have access!";
    }

    @RequestMapping("/article")
    public @ResponseBody List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @RequestMapping(value = "/article/statuses", method = RequestMethod.GET)
    public @ResponseBody List<ArticleStatus> getArticleStatuses(){
        return articleStatusRepository.findAll();
    }

    @RequestMapping(value = "/article/statuscounts/{pubId}", method = RequestMethod.GET)
    public @ResponseBody List<Integer> getStatusCountsByPubId(@PathVariable("pubId") Long pi){
        return articleService.getStatusCountsByPubId(pi);
    }

    @RequestMapping(value = "/article/reviewstatuscounts/{pubId}", method = RequestMethod.GET)
    public @ResponseBody List<Integer> getReviewStatusCountsByPubId(@PathVariable("pubId") Long pi){
        return articleService.getReviewStatusCountsByPubId(pi);
    }

    @RequestMapping(value = "/article/reviewstatuses", method = RequestMethod.GET)
    public @ResponseBody List<ArticleReview> getArticleReviewStatuses(){
        return articleReviewRepository.findAll();
    }


    @RequestMapping(value = "/publication", method = RequestMethod.GET)
    public @ResponseBody List<Publication> getAllPublications(){
        return publicationRepository.findAllByOrderByReleaseDateDesc();
    }

    @RequestMapping(value = "/publication/{pubId}/articles", method = RequestMethod.GET)
    public @ResponseBody List<Article> getArticlesInPublication(@PathVariable("pubId") Long pi){
        return articleRepository.findByPublicationId(pi);
    }

    @RequestMapping(value = "/publication/active", method = RequestMethod.GET)
    public @ResponseBody Publication getActivePublication(){
        return publicationService.getActivePublication(new Date());
    }

    @RequestMapping(value = "/publication/previous", method= RequestMethod.GET)
    public @ResponseBody Publication getPreviousPublication(){
        return publicationService.getPreviousPublication(new Date());
    }

    @RequestMapping(value = "/publication/active/articles", method = RequestMethod.GET)
    public @ResponseBody List<Article> getArticlesInActivePublication(){
        return articleRepository.findByPublicationId(publicationService.getActivePublication(new Date()).getId());
    }

    @RequestMapping(value = "/publication/layoutstatuscounts/{pubId}", method = RequestMethod.GET)
    public @ResponseBody List<Integer> getLayoutStatusCountsByPubId(@PathVariable("pubId") Long pi){
        return publicationService.getLayoutStatusCountByPublication(pi);
    }

    @RequestMapping(value = "/publication/layoutstatuses", method = RequestMethod.GET)
    public @ResponseBody List<LayoutStatus> getLayoutStatuses(){
        return layoutStatusRepository.findAll();
    }

    @RequestMapping(value = "/section", method = RequestMethod.GET)
    public @ResponseBody List<Section> getAllSections(){
        return sectionRepository.findAll();
    }

}
