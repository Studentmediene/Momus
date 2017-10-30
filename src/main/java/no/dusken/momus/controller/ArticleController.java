/*
 * Copyright 2016 Studentmediene i Trondheim AS
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.authentication.UserDetailsService;
import no.dusken.momus.diff.DiffMatchPatch;
import no.dusken.momus.diff.DiffUtil;
import no.dusken.momus.model.*;
import no.dusken.momus.service.ArticleService;
import no.dusken.momus.service.indesign.IndesignExport;
import no.dusken.momus.service.repository.*;
import no.dusken.momus.service.search.ArticleSearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
@RequestMapping("/article")
public class ArticleController {
    private Logger logger = LoggerFactory.getLogger(getClass());    

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ArticleTypeRepository articleTypeRepository;

    @Autowired
    private ArticleStatusRepository articleStatusRepository;

    @Autowired
    private ArticleRevisionRepository articleRevisionRepository;

    @Autowired
    private ArticleReviewRepository articleReviewRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private DiffUtil diffUtil;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody Article getArticleByID(@PathVariable("id") Long id) {
        Article article = articleService.getArticleRepository().findOne(id);
        if (article == null) {
            logger.warn("Article with id {} not found, tried by user {}", id, userDetailsService.getLoggedInPerson().getId());
            throw new RestException("Article " + id + " not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return article;
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Article saveArticle(@RequestBody Article article){
        return articleService.saveArticle(article);
    }

    @RequestMapping(value = "/metadata", method = RequestMethod.PATCH)
    public @ResponseBody Article updateArticle(@RequestBody Article article){
        if (articleService.getArticleRepository().findOne(article.getId()) == null) {
            throw new RestException("Article " + article.getId() + " not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return articleService.updateArticleMetadata(article);
    }

    @RequestMapping(value = "{id}/content", method = RequestMethod.GET)
    public @ResponseBody String getArticleContent(@PathVariable("id") Long id) {
        Article article = articleService.getArticleRepository().findOne(id);
        if (article == null) {
            logger.warn("Article with id {} not found, tried by user {}", id, userDetailsService.getLoggedInPerson().getId());
            throw new RestException("Article " + id + " not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return article.getContent();
    }

    @RequestMapping(value = "{id}/note", method = RequestMethod.PATCH)
    public @ResponseBody Article updateArticleNote(@PathVariable("id") Long id, @RequestBody String note){
        Article article = articleService.getArticleRepository().findOne(id);        
        if (article == null) {
            logger.warn("Article with id {} not found, tried by user {}", id, userDetailsService.getLoggedInPerson().getId());
            throw new RestException("Article " + id + " not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return articleService.updateNote(article, note);
    }

    @RequestMapping(value = "{id}/archived", method = RequestMethod.PATCH)
    public @ResponseBody Article updateArchived(@PathVariable("id") Long id, @RequestParam boolean archived) {
        Article article = articleService.getArticleRepository().findOne(id);        
        if (article == null) {
            logger.warn("Article with id {} not found, tried by user {}", id, userDetailsService.getLoggedInPerson().getId());
            throw new RestException("Article " + id + " not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return articleService.updateArchived(article, archived);
    }

    @RequestMapping(value = "/multiple", method = RequestMethod.GET)
    public @ResponseBody List<Article> getArticlesByID(@RequestParam(value="id") List<Long> ids) {
        if(ids == null) {
            return new ArrayList<>();
        }
        return articleService.getArticleRepository().findAll(ids);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public @ResponseBody List<Article> getSearchData(@RequestBody ArticleSearchParams search) {
        return articleService.searchForArticles(search);
    }

    @RequestMapping(value = "/{id}/indesignfile", method = RequestMethod.GET)
    public @ResponseBody String getIndesignExport(@PathVariable("id") Long id, HttpServletResponse response) {
        IndesignExport indesignExport = articleService.exportArticle(id);

        response.addHeader("Content-Disposition", "attachment; filename=\"" + indesignExport.getName() + ".txt\"");
        response.addHeader("Content-Type", "text/plain;charset=UTF-16LE"); // Encoding InDesign likes

        return indesignExport.getContent();
    }

    @RequestMapping(value = "/{id}/revisions", method = RequestMethod.GET)
    public @ResponseBody List<ArticleRevision> getArticleRevisions(@PathVariable("id") Long id) {
        return articleRevisionRepository.findByArticleIdOrderBySavedDateDesc(id);
    }

    @RequestMapping(value = "/{id}/revisions/{revId1}/{revId2}", method = RequestMethod.GET)
    public @ResponseBody LinkedList<DiffMatchPatch.Diff> getRevisionsDiffs(
        @PathVariable("id") Long id, 
        @PathVariable("revId1") Long revId1, 
        @PathVariable("revId2") Long revId2) {
            return diffUtil.getDiffList(id, revId1, revId2);
    }

    @RequestMapping(value = "/types", method = RequestMethod.GET)
    public @ResponseBody List<ArticleType> getAllArticleTypes(){
        return articleTypeRepository.findAll();
    }

    @RequestMapping(value = "/statuses", method = RequestMethod.GET)
    public @ResponseBody List<ArticleStatus> getAllArticleStatuses(){
        return articleStatusRepository.findAll();
    }
    @RequestMapping(value = "/sections", method = RequestMethod.GET)
    public @ResponseBody List<Section> getAllSections(){
        return sectionRepository.findAll();
    }

    @RequestMapping(value = "/reviews", method = RequestMethod.GET)
    public @ResponseBody List<ArticleReview> getAllReviewStatuses() {
        return articleReviewRepository.findAll();
    }

    @RequestMapping(value = "/statuscount", method = RequestMethod.GET)
    public @ResponseBody Map<Long,Integer> getStatusCountsByPubId(@RequestParam Long publicationId){
        List<ArticleStatus> statuses = articleStatusRepository.findAll();
        Map<Long, Integer> map = new HashMap<>();
        for (ArticleStatus status : statuses) {
            map.put(status.getId(), articleService.getArticleRepository().countByStatusIdAndPublicationId(status.getId(), publicationId));
        }
        return map;
    }

    @RequestMapping(value = "/reviewstatuscount", method = RequestMethod.GET)
    public @ResponseBody Map<Long,Integer> getReviewStatusCountsByPubId(@RequestParam Long publicationId){
        List<ArticleReview> statuses = articleReviewRepository.findAll();
        Map<Long, Integer> map = new HashMap<>();
        for (ArticleReview status : statuses) {
            map.put(status.getId(), articleService.getArticleRepository().countByReviewIdAndPublicationId(status.getId(), publicationId));
        }
        return map;
    }
}
