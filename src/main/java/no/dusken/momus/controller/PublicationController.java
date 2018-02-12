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
import no.dusken.momus.model.LayoutStatus;
import no.dusken.momus.model.Page;
import no.dusken.momus.model.Publication;
import no.dusken.momus.service.PublicationService;
import no.dusken.momus.service.repository.LayoutStatusRepository;
import no.dusken.momus.service.repository.PageRepository;
import no.dusken.momus.service.repository.PublicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/publications")
public class PublicationController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private LayoutStatusRepository layoutStatusRepository;

    @GetMapping
    public @ResponseBody List<Publication> getAllPublications(){
        return publicationRepository.findAll(new Sort(new Sort.Order(Sort.Direction.DESC, "releaseDate")));
    }

    @PostMapping
    public @ResponseBody Publication savePublication(
            @RequestBody Publication publication,
            @RequestParam(required = false, defaultValue = "50") Integer numEmptyPages
    ) {
        if(publication.getId() != null && publicationRepository.exists(publication.getId())){
            throw new RestException("Publication with given id already created. Did you mean to PUT?", HttpServletResponse.SC_BAD_REQUEST);
        }
        else if(numEmptyPages > 100){
            throw new RestException("You don't want to create that many empty pages", HttpServletResponse.SC_BAD_REQUEST);
        }

        return publicationService.savePublication(publication, numEmptyPages);
    }

    @GetMapping("/{pubid}")
    public @ResponseBody Publication getPublicationById(@PathVariable Long pubid){
        if(!publicationRepository.exists(pubid)){
            throw new RestException("Publication with given id not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return publicationRepository.findOne(pubid);
    }

    @PutMapping("/{pubid}")
    public @ResponseBody Publication updatePublication(@RequestBody Publication publication, @PathVariable Long pubid) {
        if(!publicationRepository.exists(pubid)){
            throw new RestException("Publication with given id not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return publicationService.updatePublication(publication);
    }

    @GetMapping("/active")
    public @ResponseBody Publication getActivePublication(){
        Publication active = publicationService.getActivePublication(new Date());
        if(active == null) {
            throw new RestException("No active publication found", HttpServletResponse.SC_NOT_FOUND);
        }

        return active;
    }

    @GetMapping("/{pubid}/colophon")
    public @ResponseBody void getColophon(@PathVariable Long pubid, HttpServletResponse response) throws IOException {

        response.addHeader(
                "Content-Disposition",
                "attachment; filename=\"Kolofon_" + publicationRepository.findOne(pubid).getName() + ".txt\"");
        response.addHeader("Content-Type", "text/plain;charset=UTF-8");

        ServletOutputStream outStream = response.getOutputStream();
        String colophon = publicationService.generateColophon(pubid);
        outStream.print(colophon);
        outStream.flush();
        outStream.close();
    }

    @GetMapping("/layoutstatuses")
    public @ResponseBody List<LayoutStatus> getLayoutStatuses(){
        return layoutStatusRepository.findAll();
    }

    @GetMapping("/{pubid}/pages")
    public @ResponseBody List<Page> getPagesByPublication(@PathVariable Long pubid) {
        return pageRepository.findByPublicationIdOrderByPageNrAsc(pubid);
    }

    @GetMapping("/{pubid}/pages/{pageid}")
    public @ResponseBody Page getPage(@PathVariable Long pubid, @PathVariable Long pageid){
        Page page = pageRepository.findOne(pageid);
        if(page == null){
            throw new RestException("Page with given id not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return page;
    }

    @PostMapping("/{pubid}/pages")
    public @ResponseBody List<Page> savePage(@PathVariable Long pubid, @RequestBody Page page){
        return publicationService.savePage(page);
    }

    @PostMapping("/{pubid}/pages/list")
    public @ResponseBody List<Page> savePage(@PathVariable Long pubid, @RequestBody List<Page> pages){
        int startingPageNr = pages.get(0).getPageNr();
        for(int i = 0; i < pages.size(); i++) {
            Page page = pages.get(i);
            if(page.getId() != null && pageRepository.findOne(page.getId()) == null){
                throw new RestException("Page with given id already added", HttpServletResponse.SC_BAD_REQUEST);
            }else if(page.getPageNr() != startingPageNr + i) {
                throw new RestException("Pages not following each other", HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        return publicationService.saveTrailingPages(pages);
    }

    @PutMapping("/{pubid}/pages/list")
    public @ResponseBody List<Page> updateMultiplePages(@PathVariable Long pubid, @RequestBody List<Page> pages){

        // Check that all pages exist and that the pages are following each other
        int startingPageNr = pages.get(0).getPageNr();
        for(int i = 0; i < pages.size(); i++) {
            Page page = pages.get(i);
            if(pageRepository.findOne(page.getId()) == null){
                throw new RestException("Page with given id not found", HttpServletResponse.SC_BAD_REQUEST);
            }else if(page.getPageNr() != startingPageNr + i) {
                throw new RestException("Pages not following each other", HttpServletResponse.SC_BAD_REQUEST);
            }
        }

        return publicationService.updateTrailingPages(pages);
    }

	@PatchMapping("/{pubid}/pages/{pageid}/metadata")
	public @ResponseBody Page updatePageMeta(@RequestBody Page page, @PathVariable String pubid, @PathVariable Long pageid){
		if(pageRepository.findOne(pageid) == null){
			throw new RestException("Page with given id not found", HttpServletResponse.SC_NOT_FOUND);
		}
		return publicationService.updatePageMeta(page);
    }

    @DeleteMapping("/{pubid}/pages/{pageid}")
    public @ResponseBody List<Page> deletePage(@PathVariable Long pubid, @PathVariable Long pageid){
        Page page = pageRepository.findOne(pageid);
        if(page == null){
            throw new RestException("Page with given id not found", HttpServletResponse.SC_BAD_REQUEST);
        }
        return publicationService.deletePage(page);
    }

    @GetMapping("/{pubid}/pages/layoutstatuscounts")
    public @ResponseBody Map<Long,Integer> getStatusCountsByPubId(@PathVariable Long pubid){
        List<LayoutStatus> statuses = this.getLayoutStatuses();
        Map<Long, Integer> map = new HashMap<>();
        for (LayoutStatus status : statuses) {
            map.put(status.getId(), this.getStatusCount(status.getName(), pubid));
        }
        return map;
    }

    @GetMapping("/{id}/pages/layoutstatuscounts/{status}")
    public @ResponseBody int getStatusCount(@PathVariable String status, @PathVariable Long id){
        Long statusId = layoutStatusRepository.findByName(status).getId();
        return pageRepository.countByLayoutStatusIdAndPublicationId(statusId, id);
    }
}
