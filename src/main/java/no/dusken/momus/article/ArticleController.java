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

package no.dusken.momus.article;

import no.dusken.momus.article.indesign.IndesignExport;
import no.dusken.momus.article.search.ArticleSearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

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

    @GetMapping("{id}/content")
    public ArticleContent getArticleContent(@PathVariable Long id) {
        return articleService.getArticleContent(id);
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

    @PostMapping
    public Article createArticle(@RequestBody Article article){
        return articleService.createArticle(article);
    }

    @PatchMapping("{id}/status")
    public Article updateArticleStatus(@PathVariable Long id, @RequestBody Article article) {
        return articleService.updateArticleStatus(id, article);
    }

    @PatchMapping("{id}/metadata")
    public Article updateArticleMetadata(@PathVariable Long id, @RequestBody Article article) {
        return articleService.updateArticleMetadata(id, article);
    }

    @PatchMapping("{id}/note")
    public Article updateArticleNote(@PathVariable Long id, @RequestBody String note){
        return articleService.updateNote(id, note);
    }

    @PatchMapping("{id}/archived")
    public Article updateArchived(@PathVariable Long id, @RequestParam boolean archived) {
        return articleService.updateArchived(id, archived);
    }

    @PostMapping("/search")
    public List<Article> getSearchData(@RequestBody ArticleSearchParams search) {
        return articleService.searchForArticles(search);
    }

    @GetMapping("/statuscounts")
    public Map<ArticleStatus, Integer> getStatusCountsByPubId(@RequestParam Long publicationId){
        return Arrays.stream(ArticleStatus.values())
                .collect(Collectors.toMap(
                        t -> t,
                        t -> articleRepository.countByStatusAndPublicationId(t, publicationId)));
    }

    @GetMapping("/reviewstatuscounts")
    public Map<ArticleReviewStatus,Integer> getReviewStatusCountsByPubId(@RequestParam Long publicationId){
        return Arrays.stream(ArticleReviewStatus.values())
                .collect(Collectors.toMap(
                        t -> t,
                        t -> articleRepository.countByReviewAndPublicationId(t, publicationId)));
    }
}
