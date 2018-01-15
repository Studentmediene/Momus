/*
 * Copyright 2017 Studentmediene i Trondheim AS
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

import static no.dusken.momus.util.TestUtil.toJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;

import no.dusken.momus.model.Article;
import no.dusken.momus.service.repository.ArticleRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Date;


public class ArticleControllerTest extends AbstractControllerTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    public void testGetValidArticleID() throws Exception {
        Article article = new Article();
        articleRepository.saveAndFlush(article);

        performGetExpectOk("/article/" + article.getId())
                .andExpect(jsonPath("$.id", is(article.getId().intValue())));
    }

    @Test
    public void testGetNonExistingArticleID() throws Exception {
        this.mockMvc.perform(get("/article/2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testSaveArticle() throws Exception {
        /*  TODO: mock something somewhere so that the code
            which creates the drive document doesn't crash
        */
        Article article = new Article();
        article.setLastUpdated(new Date());
        String articleJSON = toJsonString(article);
        mockMvc.perform(post("/article")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(articleJSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testGetContent() throws Exception {
        Article article = new Article();
        String content = "This is some test content";
        article.setContent(content);
        articleRepository.saveAndFlush(article);

        performGetExpectOk("/article/" + article.getId() + "/content")
                .andExpect(jsonPath("$", is(content)));
    }
}
