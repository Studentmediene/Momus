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

package no.dusken.momus.newsitem;

import org.springframework.web.bind.annotation.*;

import no.dusken.momus.person.authorization.AdminAuthorization;

import java.util.List;

@RestController
@RequestMapping("/api/newsitem")
public class NewsItemController {
    private final NewsItemService newsItemService;

    public NewsItemController(NewsItemService newsItemService) {
        this.newsItemService = newsItemService;
    }

    @GetMapping
    public List<NewsItem> getAllNewsItems() {
        return newsItemService.getAllNewsItems();
    }

    @GetMapping("/{newsItemId}")
    public NewsItem getNewsItem(@PathVariable Long newsItemId){
        return newsItemService.getNewsItemById(newsItemId);
    }

    @PostMapping
    @AdminAuthorization
    public NewsItem createNewsItem(@RequestBody NewsItem newsItem) {
        return newsItemService.createNewsItem(newsItem);
    }

    @PutMapping("/{newsItemId}")
    @AdminAuthorization
    public NewsItem updateNewsItem(@RequestBody NewsItem newsItem, @PathVariable Long newsItemId) {
        return newsItemService.updateNewsItem(newsItemId, newsItem);
    }


}
