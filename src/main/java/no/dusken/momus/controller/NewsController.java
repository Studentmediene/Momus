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
import no.dusken.momus.service.NewsItemService;
import no.dusken.momus.service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;
import java.util.List;


@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    NewsItemRepository newsItemRepository;

    @Autowired
    NewsItemService newsItemService;

    @GetMapping
    public @ResponseBody List<NewsItem> getAllNewsItems() {
        return newsItemRepository.findAll();
    }

    @PostMapping
    @AdminAuthorization
    public @ResponseBody NewsItem saveNewsItem(@RequestBody NewsItem newsItem) {
        newsItem.setDate(ZonedDateTime.now());
        return newsItemRepository.save(newsItem);
    }

    @GetMapping("/{newsItemId}")
    public @ResponseBody NewsItem getNewsItem(@PathVariable Long newsItemId){
        NewsItem newsItem = newsItemRepository.findOne(newsItemId);
        if(newsItem == null){
            throw new RestException("NewsItem with given id not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return newsItem;
    }

    @PutMapping("/{newsItemId}")
    @AdminAuthorization
    public @ResponseBody NewsItem updateNewsItem(@RequestBody NewsItem newsItem, @PathVariable Long newsItemId) {
        if(!newsItemRepository.exists(newsItemId)){
            throw new RestException("NewsItem with given id not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return newsItemService.updateNewsItem(newsItem);
    }


}
