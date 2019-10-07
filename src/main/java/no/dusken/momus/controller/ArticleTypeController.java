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

import no.dusken.momus.authorization.AdminAuthorization;
import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.*;
import no.dusken.momus.service.ArticleTypeService;
import no.dusken.momus.service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/articletype")
public class ArticleTypeController {
    private final ArticleTypeService articleTypeService;

    public ArticleTypeController(ArticleTypeService service) {
        this.articleTypeService = service;
    }

    @GetMapping
    public @ResponseBody List<ArticleType> getAllArticleTypes(){
        return articleTypeService.getAllArticleTypes();
    }

    @PostMapping
    @AdminAuthorization
    public @ResponseBody ArticleType createArticleType(@RequestBody ArticleType articleType){
        return articleTypeService.createArticleType(articleType);
    }

    @PatchMapping
    @AdminAuthorization
    public @ResponseBody ArticleType updateArticleType(@PathVariable Long id, @RequestBody ArticleType articleType){
        return articleTypeService.updateArticleType(id, articleType);
    }
}
