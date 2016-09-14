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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

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
        return articleService.getArticleById(id);
    }


    @RequestMapping(value = "/multiple", method = RequestMethod.POST)
    public @ResponseBody List<Article> getArticleByID(@RequestBody List<Long> ids) {
        return articleService.getArticleRepository().findAll(ids);
    }


    @RequestMapping(value = "/publication/{id}", method = RequestMethod.GET)
    public @ResponseBody List<Article> getAllArticlesByPublicationID(@PathVariable("id") Long id) {
        return articleService.getArticleRepository().findByPublicationId(id);
    }


    @RequestMapping(value = "/{id}/export", method = RequestMethod.GET)
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

    @RequestMapping(value = "/{articleId}/revisions/{revId1}/{revId2}", method = RequestMethod.GET)
    public @ResponseBody
    LinkedList<DiffMatchPatch.Diff> getRevisionsDiffs(@PathVariable("articleId") Long articleId, @PathVariable("revId1") Long revId1, @PathVariable("revId2") Long revId2) {
        return diffUtil.getDiffList(articleId, revId1, revId2);
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


    @RequestMapping(value = "/metadata", method = RequestMethod.PUT)
    public @ResponseBody Article updateArticleMetadata(@RequestBody Article article){
        return articleService.saveMetadata(article);
    }

    @RequestMapping(value = "/note", method = RequestMethod.PUT)
    public @ResponseBody Article updateArticleNote(@RequestBody Article article){
        return articleService.saveNote(article);
    }


    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public @ResponseBody List<Article> getSearchData(@RequestBody ArticleSearchParams search) {
        return articleService.searchForArticles(search);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody Article deleteArticle(@RequestBody Article article) {
        return articleService.archiveArticle(article);
    }

    @RequestMapping(value = "/restore", method = RequestMethod.POST)
    public @ResponseBody Article restoreArticle(@RequestBody Article article) {
        return articleService.restoreArticle(article);
    }

    @RequestMapping(value = "/reviews", method = RequestMethod.GET)
    public @ResponseBody List<ArticleReview> getAllReviewStatuses() { return articleReviewRepository.findAll(); }

    @RequestMapping(value = "/statuscount/{pubId}/{statId}", method = RequestMethod.GET)
    public @ResponseBody int getStatusCount(@PathVariable("statId") Long as, @PathVariable("pubId") Long pi){
        return articleService.getArticleRepository().countByStatusIdAndPublicationId(as, pi);
    }

    @RequestMapping(value = "/statuscount/{pubId}", method = RequestMethod.GET)
    public @ResponseBody List<Integer> getStatusCountsByPubId(@PathVariable("pubId") Long pi){
        List<ArticleStatus> statuses = this.getAllArticleStatuses();
        List<Integer> list = new ArrayList<Integer>();
        for(int i = 1; i <= statuses.size(); i++){
            list.add(this.getStatusCount(Long.valueOf(i), pi));
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Article createArticle(@RequestBody Article article){
        return articleService.createNewArticle(article);
    }

}
