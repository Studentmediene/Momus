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

package no.dusken.momus.publication;

import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/publications")
public class PublicationController {
    private final PublicationService publicationService;
    private final LayoutStatusRepository layoutStatusRepository;

    public PublicationController(
        PublicationService publicationService,
        LayoutStatusRepository layoutStatusRepository
    ) {
        this.publicationService = publicationService;
        this.layoutStatusRepository = layoutStatusRepository;
    }

    @GetMapping
    public List<Publication> getAllPublications(){
        return publicationService.getAllPublications();
    }

    @PostMapping
    public Publication createPublication(
            @RequestBody Publication publication,
            @RequestParam(required = false, defaultValue = "50") Integer numEmptyPages
    ) {
        return publicationService.createPublication(publication, numEmptyPages);
    }

    @GetMapping("/{pubid}")
    public Publication getPublicationById(@PathVariable Long pubid){
        return publicationService.getPublicationById(pubid);
    }

    @PatchMapping("/{pubid}/metadata")
    public Publication updatePublicationMetadata(@PathVariable Long pubid, @RequestBody Publication publication) {
        return publicationService.updatePublicationMetadata(pubid, publication);
    }

    @GetMapping("/active")
    public Publication getActivePublication(){
        return publicationService.getActivePublication(LocalDate.now());
    }

    @GetMapping("/{pubid}/colophon")
    public void getColophon(@PathVariable Long pubid, HttpServletResponse response) throws IOException {

        response.addHeader(
                "Content-Disposition",
                "attachment; filename=\"Kolofon_" + publicationService.getPublicationById(pubid).getName() + ".txt\"");
        response.addHeader("Content-Type", "text/plain;charset=UTF-8");

        ServletOutputStream outStream = response.getOutputStream();
        String colophon = publicationService.generateColophon(pubid);
        outStream.print(colophon);
        outStream.flush();
        outStream.close();
    }

    @GetMapping("/layoutstatuses")
    public List<LayoutStatus> getLayoutStatuses(){
        return layoutStatusRepository.findAll();
    }
}
