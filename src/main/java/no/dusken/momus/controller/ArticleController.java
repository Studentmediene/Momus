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
import no.dusken.momus.dto.ArticleContent;
import no.dusken.momus.model.*;
import no.dusken.momus.service.ArticleService;
import no.dusken.momus.service.indesign.IndesignExport;
import no.dusken.momus.service.repository.*;
import no.dusken.momus.service.search.ArticleSearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private DiffUtil diffUtil;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleTypeRepository articleTypeRepository;

    @Autowired
    private ArticleStatusRepository articleStatusRepository;

    @Autowired
    private ArticleReviewRepository articleReviewRepository;

    @Autowired
    private ArticleRevisionRepository articleRevisionRepository;

    @GetMapping(params="userId")
    public List<Article> getLastArticlesForUser(@RequestParam Long userId) {
        return articleService.getLastArticlesForUser(userId);
    }

    @GetMapping(params="publicationId")
    public List<Article> getArticlesInPublication(@RequestParam Long publicationId) {
        return articleService.getArticlesInPublication(publicationId);
    }

    @GetMapping("/{id}")
    public Article getArticleById(@PathVariable Long id) {
        return articleService.getArticleById(id);
    }

    @GetMapping("/multiple")
    public List<Article> getArticlesByIds(@RequestParam(value="ids") List<Long> ids) {
        return articleService.getArticlesByIds(ids);
    }

    @PostMapping
    public Article createArticle(@RequestBody Article article){
        return articleService.createArticle(article);
    }

    @PatchMapping("{id}/status")
    public Article updateArticleStatus(@PathVariable Long id, @RequestBody Article article){
        return articleService.updateArticleStatus(id, article);
    }

    @PatchMapping("{id}/metadata")
    public Article updateArticleMetadata(@PathVariable Long id, @RequestBody Article article){
        return articleService.updateArticleMetadata(id, article);
    }

    @GetMapping("{id}/content")
    public ArticleContent getArticleContent(@PathVariable Long id) {
        return articleService.getArticleContent(id);
    }

    @PatchMapping("{id}/note")
    public Article updateArticleNote(@PathVariable Long id, @RequestBody String note){
        return articleService.updateNote(id, note);
    }

    @PatchMapping("{id}/archived")
    public Article updateArchived(@PathVariable Long id, @RequestParam boolean archived) {
        return articleService.updateArchived(id, archived);
    }

    @GetMapping("/{id}/indesignfile")
    public void getIndesignExport(@PathVariable Long id, HttpServletResponse response) throws IOException {
        IndesignExport indesignExport = articleService.exportArticle(id);

        response.addHeader("Content-Disposition", "attachment; filename=\"" + indesignExport.getName() + ".txt\"");
        response.addHeader("Content-Type", "text/plain;charset=UTF-16LE"); // Encoding InDesign likes

        ServletOutputStream outStream = response.getOutputStream();
        String exportContent = indesignExport.getContent();
        outStream.print(exportContent);
        outStream.flush();
        outStream.close();
    }

    @GetMapping("/{id}/revisions")
    public List<ArticleRevision> getArticleRevisions(@PathVariable Long id) {
        return articleRevisionRepository.findByArticleIdOrderBySavedDateDesc(id);
    }

    @GetMapping("/{id}/revisions/{revId1}/{revId2}")
    public List<DiffMatchPatch.Diff> getRevisionsDiffs(
            @PathVariable Long id, @PathVariable Long revId1, @PathVariable Long revId2) {
        return diffUtil.getDiffList(id, revId1, revId2);
    }

    @PostMapping("/search")
    public List<Article> getSearchData(@RequestBody ArticleSearchParams search) {
        return articleService.searchForArticles(search);
    }

    @GetMapping("/types")
    public List<ArticleType> getAllArticleTypes(){
        return articleTypeRepository.findAll();
    }

    @GetMapping("/statuses")
    public List<ArticleStatus> getAllArticleStatuses(){
        return articleStatusRepository.findAll();
    }

    @GetMapping("/reviews")
    public List<ArticleReview> getAllReviewStatuses() {
        return articleReviewRepository.findAll();
    }

    @GetMapping("/statuscounts")
    public Map<Long,Integer> getStatusCountsByPubId(@RequestParam Long publicationId){
        return getAllArticleStatuses().stream().map(ArticleStatus::getId)
                .collect(Collectors.toMap(
                        t -> t,
                        t -> articleRepository.countByStatusIdAndPublicationId(t, publicationId)));
    }

    @GetMapping("/reviewstatuscounts")
    public Map<Long,Integer> getReviewStatusCountsByPubId(@RequestParam Long publicationId){
        return getAllReviewStatuses().stream().map(ArticleReview::getId)
                .collect(Collectors.toMap(
                        t -> t,
                        t -> articleRepository.countByReviewIdAndPublicationId(t, publicationId)));
    }
}
