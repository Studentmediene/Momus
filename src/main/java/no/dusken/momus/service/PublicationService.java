/*
 * Copyright 2014 Studentmediene i Trondheim AS
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


import no.dusken.momus.model.Page;
import no.dusken.momus.model.Publication;
import no.dusken.momus.service.repository.PageRepository;
import no.dusken.momus.service.repository.PublicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicationService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PageRepository pageRepository;

    public Publication savePublication(Publication publication){
        Publication savedPublication = publicationRepository.findOne(publication.getId());

        savedPublication.setName(publication.getName());
        savedPublication.setReleaseDate(publication.getReleaseDate());

        if(publication.getPages() != null){
            for (Page p : publication.getPages()){
                Page savedPage;
                if(p.getId() != null){
                    savedPage = pageRepository.findOne(p.getId());
                }else{
                    savedPage = new Page();
                }
                savedPage.setAdvertisement(p.isAdvertisement());
                savedPage.setPageNr(p.getPageNr());
                savedPage.setNote(p.getNote());
                savedPage.setPublication(p.getPublication());
                savedPage.setArticles(p.getArticles());
                pageRepository.save(savedPage);
            }
        }

        savedPublication = publicationRepository.save(savedPublication);

        logger.info("Updated publication {} with data: {}", publication.getName(), publication);

        return publicationRepository.findOne(savedPublication.getId());
    }

    public void deletePagesInPublication(Long id){
        List<Page> pages = pageRepository.findByPublicationIdOrderByPageNrAsc(id);
        for(Page p: pages){
            pageRepository.delete(p);
        }
        logger.info("Deleted all the pages from publication with id: " + id);
    }
}
