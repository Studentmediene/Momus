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

    import ch.qos.logback.core.net.SyslogOutputStream;
    import no.dusken.momus.authorization.AdminAuthorization;
    import no.dusken.momus.authorization.Role;
    import org.springframework.security.access.prepost.PreAuthorize;
    import no.dusken.momus.diff.DiffMatchPatch;
    import no.dusken.momus.diff.DiffUtil;
    import no.dusken.momus.exceptions.RestException;
    import no.dusken.momus.model.*;
    import no.dusken.momus.service.ArticleService;
    import no.dusken.momus.service.NewsService;
    import no.dusken.momus.service.indesign.IndesignExport;
    import no.dusken.momus.service.repository.*;
    import no.dusken.momus.service.search.ArticleSearchParams;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    import javax.servlet.ServletOutputStream;
    import javax.servlet.http.HttpServletResponse;
    import java.io.IOException;
    import java.time.LocalDate;
    import java.util.Date;
    import java.util.List;
    import java.util.Map;
    import java.util.stream.Collectors;


@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    NewsRepository newsRepository;

    @Autowired
    NewsService newsService;

    @GetMapping
    @AdminAuthorization
    public @ResponseBody List<News> getAllNews() {
        return newsRepository.findAll();
    }

    @PostMapping
    @AdminAuthorization
    public @ResponseBody News saveNews(@RequestBody News news) {
        news.setDate(new Date());
        return newsRepository.save(news);
    }

    @GetMapping("/{newsid}")
    @AdminAuthorization
    public @ResponseBody News getNews(@PathVariable Long newsid){
        News news = newsRepository.findOne(newsid);
        if(news == null){
            throw new RestException("News with given id not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return news;
    }

    @PutMapping("/{newsid}")
    @AdminAuthorization
    public @ResponseBody News updateNews(@RequestBody News news, @PathVariable Long newsid) {
        if(!newsRepository.exists(newsid)){
            throw new RestException("News with given id not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return newsService.updateNews(news);
    }


}
