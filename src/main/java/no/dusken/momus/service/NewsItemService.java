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

package no.dusken.momus.service;

import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.*;
import no.dusken.momus.service.repository.*;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class NewsItemService {
    private final NewsItemRepository newsItemRepository;

    public NewsItemService(NewsItemRepository newsItemRepository) {
        this.newsItemRepository = newsItemRepository;
    }

    public List<NewsItem> getAllNewsItems() {
        return newsItemRepository.findAll();
    }

    public NewsItem getNewsItemById(Long id) {
        return newsItemRepository.findById(id)
            .orElseThrow(() -> new RestException("Not found", 404));
    }

    public NewsItem createNewsItem(NewsItem item) {
        item.setDate(ZonedDateTime.now());
        return newsItemRepository.saveAndFlush(item);
    }
    public NewsItem updateNewsItem(Long id, NewsItem newsItem){
        NewsItem existing = getNewsItemById(id);
        existing.setContent(newsItem.getContent());
        existing.setTitle(newsItem.getTitle());

        existing = newsItemRepository.save(existing);

        log.info("Updated newsItem {}.", existing.getTitle());

        return existing;
    }
}
