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

    @Autowired
    private ArticleTypeService articleTypeService;

    @Autowired
    private ArticleTypeRepository articleTypeRepository;

    @GetMapping
    public @ResponseBody List<ArticleType> getAllArticleTypes(){
        return articleTypeRepository.findAll();
    }


    @GetMapping("/{id}")
    public @ResponseBody ArticleType getArticleTypeByID(@PathVariable Long id) {
        return articleTypeService.getArticleTypeById(id);
    }

    @PostMapping
    @AdminAuthorization
    public @ResponseBody ArticleType saveArticleType(@RequestBody ArticleType articleType){
        return articleTypeService.saveArticleType(articleType);
    }

    @PatchMapping("{id}/name")
    @AdminAuthorization
    public @ResponseBody ArticleType updateName(@PathVariable Long id, @RequestBody String name){
        return articleTypeService.updateName(id, name);
    }
    
    @PatchMapping("{id}/description")
    @AdminAuthorization
    public @ResponseBody ArticleType updateDescription(@PathVariable Long id, @RequestBody String description){
        return articleTypeService.updateDescription(id, description);
    }

    @PatchMapping("{id}/deleted")
    @AdminAuthorization
    public @ResponseBody ArticleType updateDeleted(@PathVariable Long id, @RequestParam boolean deleted) {
        return articleTypeService.updateDeleted(id, deleted);
    }

    @PutMapping("/{id}")
    @AdminAuthorization
    public @ResponseBody ArticleType updateArticleType(@RequestBody ArticleType articleType, @PathVariable Long id) {
        if(!articleTypeRepository.exists(id)){
            throw new RestException("ArticleType with given id not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return articleTypeService.updateArticleType(articleType);
    }

}
