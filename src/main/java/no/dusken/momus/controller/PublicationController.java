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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequestMapping("/publication")
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

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Publication> getAllPublications(){
        return publicationRepository.findAll(new Sort(new Sort.Order(Sort.Direction.DESC, "releaseDate")));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody Publication getPublicationById(@PathVariable("id") Long id){
        return publicationRepository.findOne(id);
    }

    @RequestMapping(value = "/metadata", method = RequestMethod.PUT)
    public @ResponseBody Publication savePublication(@RequestBody Publication publication) {
        return publicationService.savePublication(publication);
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Publication addPublication(@RequestBody Publication publication) {
        Publication newPublication = publicationRepository.save(publication);
        newPublication = publicationRepository.findOne(newPublication.getId());
        for(int i = 0; i < 64; i++){
            Page newPage = new Page();
            newPage.setPageNr(i + 1);
            newPage.setPublication(newPublication);
            newPage.setLayoutStatus(layoutStatusRepository.findByName("Ukjent"));
            pageRepository.save(newPage);
        }
        logger.info("Created new publication with data: {}", newPublication);

        return newPublication;
    }

    @RequestMapping(value = "/pages/{id}", method = RequestMethod.GET)
    public @ResponseBody List<Page> getPagesByPublication(@PathVariable("id") Long id) {
        return pageRepository.findByPublicationIdOrderByPageNrAsc(id);
    }

    @RequestMapping(value = "pages/", method = RequestMethod.POST)
    public @ResponseBody Page createPage(@RequestBody Page page){
        return pageRepository.saveAndFlush(page);
    }

    @RequestMapping(value = "pages/delete/{id}", method = RequestMethod.DELETE)
    public @ResponseBody void deletePage(@PathVariable("id") Long id){
        pageRepository.delete(pageRepository.findOne(id));
    }

    @RequestMapping(value = "pages/generate/{id}", method = RequestMethod.GET)
    public @ResponseBody List<Page> generateDisp(@PathVariable("id") Long id){
        return publicationService.generateDisp(id);
    }

    @RequestMapping(value = "/layoutstatus", method = RequestMethod.GET)
    public @ResponseBody List<LayoutStatus> getLayoutStatuses(){
        return layoutStatusRepository.findAll();
    }
}
