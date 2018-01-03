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

import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.Article;
import no.dusken.momus.model.LayoutStatus;
import no.dusken.momus.model.Page;
import no.dusken.momus.model.Publication;
import no.dusken.momus.service.ArticleService;
import no.dusken.momus.service.PublicationService;
import no.dusken.momus.service.repository.LayoutStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
@RequestMapping("/publications")
public class PublicationController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private LayoutStatusRepository layoutStatusRepository;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Publication> getAllPublications(){
        return publicationService.getPublicationRepository().findAll(new Sort(new Sort.Order(Sort.Direction.DESC, "releaseDate")));
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Publication savePublication(@RequestBody Publication publication, @RequestParam(required = false, defaultValue = "50") Integer numEmptyPages) {
        if(publication.getId() != null && publicationService.getPublicationRepository().findOne(publication.getId()) != null){
            throw new RestException("Publication with given id already created. Did you mean to PUT?", HttpServletResponse.SC_BAD_REQUEST);
        }
        else if(numEmptyPages > 100){
            throw new RestException("You don't want to create that many empty pages", HttpServletResponse.SC_BAD_REQUEST);
        }

        return publicationService.savePublication(publication, numEmptyPages);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public @ResponseBody Publication updatePublication(@RequestBody Publication publication) {
        if(publicationService.getPublicationRepository().findOne(publication.getId()) == null){
            throw new RestException("Publication with given id not found", HttpServletResponse.SC_BAD_REQUEST);
        }
        return publicationService.updatePublication(publication);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody Publication getPublicationById(@PathVariable("id") Long id){
        return publicationService.getPublicationRepository().findOne(id);
    }

    @RequestMapping(value = "/active", method = RequestMethod.GET)
    public @ResponseBody Publication getActivePublication(){
        Publication active = publicationService.getActivePublication(new Date());
        if(active == null)
            throw new RestException("No active publication found", HttpServletResponse.SC_NOT_FOUND);

        return active;
    }

    @RequestMapping(value = "/{id}/colophon", method = RequestMethod.GET)
    public @ResponseBody void getColophon(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {

        response.addHeader("Content-Disposition", "attachment; filename=\"Kolofon_" + publicationService.getPublicationRepository().findOne(id).getName() + ".txt\"");
        response.addHeader("Content-Type", "text/plain;charset=UTF-8");

        ServletOutputStream outStream = response.getOutputStream();
        String colophon = publicationService.generateColophon(id);
        outStream.print(colophon);
        outStream.flush();
        outStream.close();
    }

    @RequestMapping(value = "/layoutstatuses", method = RequestMethod.GET)
    public @ResponseBody List<LayoutStatus> getLayoutStatuses(){
        return layoutStatusRepository.findAll();
    }

    @RequestMapping(value = "{id}/pages", method = RequestMethod.GET)
    public @ResponseBody List<Page> getPagesByPublication(@PathVariable("id") Long id) {
        return publicationService.getPageRepository().findByPublicationIdOrderByPageNrAsc(id);
    }

    @RequestMapping(value = "{id}/pages", method = RequestMethod.POST)
    public @ResponseBody List<Page> savePage(@PathVariable("id") Long id, @RequestBody Page page){
        return publicationService.savePage(page);
    }

    @RequestMapping(value = "{id}/pages/list", method = RequestMethod.POST)
    public @ResponseBody List<Page> savePage(@PathVariable("id") Long id, @RequestBody List<Page> pages){
        int startingPageNr = pages.get(0).getPageNr();
        for(int i = 0; i < pages.size(); i++) {
            Page page = pages.get(i);
            if(page.getId() != null && publicationService.getPageRepository().findOne(page.getId()) == null){
                throw new RestException("Page with given id already added", HttpServletResponse.SC_BAD_REQUEST);
            }else if(page.getPageNr() != startingPageNr + i) {
                throw new RestException("Pages not following each other", HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        return publicationService.saveTrailingPages(pages);
    }

    @RequestMapping(value = "{id}/pages", method = RequestMethod.PUT)
    public @ResponseBody List<Page> updatePage(@RequestBody Page page){
        if(publicationService.getPageRepository().findOne(page.getId()) == null){
            throw new RestException("Page with given id not found", HttpServletResponse.SC_BAD_REQUEST);
        }
        return publicationService.updatePage(page);
    }

    @RequestMapping(value = "{id}/pages/list", method = RequestMethod.PUT)
    public @ResponseBody List<Page> updateMultiplePages(@PathVariable("id") Long id, @RequestBody List<Page> pages){

        // Check that all pages exist and that the pages are following each other
        int startingPageNr = pages.get(0).getPageNr();
        for(int i = 0; i < pages.size(); i++) {
            Page page = pages.get(i);
            if(publicationService.getPageRepository().findOne(page.getId()) == null){
                throw new RestException("Page with given id not found", HttpServletResponse.SC_BAD_REQUEST);
            }else if(page.getPageNr() != startingPageNr + i) {
                throw new RestException("Pages not following each other", HttpServletResponse.SC_BAD_REQUEST);
            }
        }

        return publicationService.updateTrailingPages(pages);
    }

	@RequestMapping(value = "{id}/pages/{pageid}", method = RequestMethod.PATCH)
	public @ResponseBody Page updatePageMeta(@RequestBody Page page, @PathVariable("pageid") Long pageid){
		if(publicationService.getPageRepository().findOne(pageid) == null){
			throw new RestException("Page with given id not found", HttpServletResponse.SC_BAD_REQUEST);
		}
		return publicationService.updatePageMeta(page);
    }
    
    @RequestMapping(value = "{pubid}/pages/{pageid}", method = RequestMethod.GET)
    public @ResponseBody Page getPage(@PathVariable("pubid") Long pubid, @PathVariable("pageid") Long pageid){
        Page page = publicationService.getPageRepository().findOne(pageid);
        if(page == null){
            throw new RestException("Page with given id not found", HttpServletResponse.SC_BAD_REQUEST);
        }
        return page;
    }

    @RequestMapping(value = "{pubid}/pages/{pageid}", method = RequestMethod.DELETE)
    public @ResponseBody List<Page> deletePage(@PathVariable("pubid") Long pubid, @PathVariable("pageid") Long pageid){
        Page page = publicationService.getPageRepository().findOne(pageid);
        if(page == null){
            throw new RestException("Page with given id not found", HttpServletResponse.SC_BAD_REQUEST);
        }
        return publicationService.deletePage(page);
    }

    @RequestMapping(value = "{id}/pages/layoutstatuscounts", method = RequestMethod.GET)
    public @ResponseBody Map<Long,Integer> getStatusCountsByPubId(@PathVariable("id") Long id){
        List<LayoutStatus> statuses = this.getLayoutStatuses();
        Map<Long, Integer> map = new HashMap<>();
        for (LayoutStatus status : statuses) {
            map.put(status.getId(), this.getStatusCount(status.getName(), id));
        }
        return map;
    }

    @RequestMapping(value = "{id}/pages/layoutstatuscounts/{status}", method = RequestMethod.GET)
    public @ResponseBody int getStatusCount(@PathVariable("status") String status, @PathVariable("id") Long id){
        Long statusId = layoutStatusRepository.findByName(status).getId();
        return publicationService.getPageRepository().countByLayoutStatusIdAndPublicationId(statusId, id);
    }

    @RequestMapping(value = "{id}/articles", method = RequestMethod.GET)
    public @ResponseBody List<Article> getArticlesInPublication(@PathVariable("id") Long id){
        return articleService.getArticleRepository().findByPublicationId(id);
    }
}
