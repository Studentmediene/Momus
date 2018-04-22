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

import no.dusken.momus.model.*;
import no.dusken.momus.service.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class NewsService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NewsRepository newsRepository;

    public News updateNews(News news){
        news = newsRepository.save(news);

        logger.info("Updated publication {}.", news.getTitle());

        // It seems to be necessary to reaccess the publication or else the releaseDate is not returned in the proper format.
        return newsRepository.findOne(news.getId());
    }
}
