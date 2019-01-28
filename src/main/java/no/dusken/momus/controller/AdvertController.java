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

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.dusken.momus.model.Advert;
import no.dusken.momus.service.AdvertService;

@RestController
@RequestMapping("/api/advert")
public class AdvertController {
    private final AdvertService advertService;

    public AdvertController(AdvertService advertService) {
        this.advertService = advertService;
    }

    @GetMapping
    public List<Advert> getAllAdverts(){
        return advertService.getAllAdverts();
    }

    @GetMapping("/{id}")
    public Advert getAdvertByID(@PathVariable Long id) {
        return advertService.getAdvertById(id);
    }

    @PostMapping
    public Advert createAdvert(@RequestBody Advert advert){
        return advertService.createAdvert(advert);
    }

    @PatchMapping("{id}/comment")
    public Advert updateComment(@PathVariable Long id, @RequestBody String comment){
        return advertService.updateComment(id, comment);
    }

}
