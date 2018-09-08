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

import com.fasterxml.jackson.annotation.JsonView;
import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.mapper.SerializationViews;
import no.dusken.momus.model.LayoutStatus;
import no.dusken.momus.model.Publication;
import no.dusken.momus.service.PublicationService;
import no.dusken.momus.service.repository.LayoutStatusRepository;
import no.dusken.momus.service.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/publications")
public class PublicationController {
    @Autowired
    private PublicationService publicationService;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private LayoutStatusRepository layoutStatusRepository;

    @GetMapping
    @JsonView(SerializationViews.Simple.class)
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
        Publication active = publicationService.getActivePublication(LocalDate.now());
        if(active == null) {
            throw new RestException("No active publication found", HttpServletResponse.SC_NOT_FOUND);
        }

        return active;
    }

    @GetMapping("/active/simple")
    @JsonView(SerializationViews.Simple.class)
    public @ResponseBody Publication getSimpleActivePublication() {
        return getActivePublication();
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
}
