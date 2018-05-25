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


import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.model.*;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.LayoutStatusRepository;
import no.dusken.momus.service.repository.PageRepository;
import no.dusken.momus.service.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class PublicationService {
    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PageService pageService;

    @Autowired
    private ArticleRepository articleRepository;

    public Publication savePublication(Publication publication, Integer numEmptyPages){
        Publication newPublication = publicationRepository.saveAndFlush(publication);

        pageService.createEmptyPagesInPublication(newPublication.getId(), 0, numEmptyPages);

        log.info("Created new publication with data: {}", newPublication);

        return newPublication;
    }

    public Publication updatePublication(Publication publication){
        log.info("Updated publication {} with data: {}", publication.getName(), publication);

        return publicationRepository.saveAndFlush(publication);
    }

    /**
     * Generates a string containing the people who have contributed to a publication
     * @param pubId Id of the pulication to generate colophon from
     * @return The generated colophon
     */
    public String generateColophon(Long pubId){
        List<Article> articles = articleRepository.findByPublicationId(pubId);

        Set<Person> journalists = new HashSet<>();
        Set<Person> photographers = new HashSet<>();
        Set<Person> illustrators = new HashSet<>();

        for(Article a : articles){
            journalists.addAll(a.getJournalists());
            if(a.isUseIllustration()){
                illustrators.addAll(a.getPhotographers());
            }else{
                photographers.addAll(a.getPhotographers());
            }
        }
        StringBuilder colophonBuilder = new StringBuilder();

        colophonBuilder.append("Journalister:\r\n");
        for(Person p : journalists){
            colophonBuilder.append(p.getName()).append("\r\n");
        }

        colophonBuilder.append("\r\nFotografer:\r\n");
        for(Person p : photographers){
            colophonBuilder.append(p.getName()).append("\r\n");
        }

        colophonBuilder.append("\r\nIllustratører:\r\n");
        for(Person p : illustrators){
            colophonBuilder.append(p.getName()).append("\r\n");
        }
        return colophonBuilder.toString();
    }

    /**
     *
     * @return Returns the oldest publication that has not been released yet at the time of the date parameter
     */
    public Publication getActivePublication(LocalDate date){
        List<Publication> publications = publicationRepository.findAllByOrderByReleaseDateDesc();

        if(publications.isEmpty()) return null;

        if(publications.size() == 1) return publications.get(0);

        Publication active = publications.get(0);
        for(Publication p : publications.subList(1,publications.size())){
            if(p.getReleaseDate().isBefore(date)){
                return active;
            }else{
                active = p;
            }
        }
        return active;
    }
}
