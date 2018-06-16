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

import no.dusken.momus.model.*;
import no.dusken.momus.service.AdvertService;
import no.dusken.momus.service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/advert")
public class AdvertController {

    @Autowired
    private AdvertService advertService;

    @Autowired
    private AdvertRepository advertRepository;

    @GetMapping
    public @ResponseBody List<Advert> getAllAdverts(){
        return advertRepository.findAll();
    }


    @GetMapping("/{id}")
    public @ResponseBody Advert getAdvertByID(@PathVariable Long id) {
        return advertService.getAdvertById(id);
    }

    @PostMapping
    public @ResponseBody Advert saveAdvert(@RequestBody Advert advert){
        return advertService.saveAdvert(advert);
    }

    @PatchMapping("{id}/comment")
    public @ResponseBody Advert updateComment(@PathVariable Long id, @RequestBody String comment){
        return advertService.updateComment(id, comment);
    }

}