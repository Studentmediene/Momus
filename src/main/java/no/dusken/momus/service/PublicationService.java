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
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.LayoutStatusRepository;
import no.dusken.momus.service.repository.PageRepository;
import no.dusken.momus.service.repository.PublicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PublicationService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private LayoutStatusRepository layoutStatusRepository;

    public Publication savePublication(Publication publication, Integer numEmptyPages){
        Publication newPublication = publicationRepository.save(publication);
        newPublication = publicationRepository.findOne(newPublication.getId());
        for(int i = 0; i < numEmptyPages; i++){
            Page newPage = new Page();
            newPage.setPageNr(i + 1);
            newPage.setPublication(newPublication);
            newPage.setLayoutStatus(layoutStatusRepository.findByName("Ukjent"));
            pageRepository.save(newPage);
        }
        logger.info("Created new publication with data: {}", newPublication);

        return newPublication;
    }

    public Publication updatePublication(Publication publication){
        publication = publicationRepository.save(publication);

        logger.info("Updated publication {} with data: {}", publication.getName(), publication);

        // It seems to be necessary to reaccess the publication or else the releaseDate is not returned in the proper format.
        return publicationRepository.findOne(publication.getId());
    }

    /**
     * Generates a string containing the people who have contributed to a publication
     * @param pubId
     * @return
     */
    public String generateColophon(Long pubId){
        List<Article> articles = articleRepository.findByPublicationId(pubId);

        Set<Person> journalists = new HashSet<>();
        Set<Person> photographers = new HashSet<>();
        Set<Person> illustrators = new HashSet<>();

        for(Article a : articles){
            journalists.addAll(a.getJournalists());
            if(a.getUseIllustration()){
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
    public Publication getActivePublication(Date date){
        List<Publication> publications = publicationRepository.findAllByOrderByReleaseDateDesc();

        if(publications.isEmpty()) return null;

        if(publications.size() == 1) return publications.get(0);

        Publication active = publications.get(0);
        for(Publication p : publications.subList(1,publications.size())){
            if(p.getReleaseDate().before(date)){
                return active;
            }else{
                active = p;
            }
        }
        return active;
    }

    public List<Page> savePage(Page page){
        List<Page> pages = pageRepository.findByPublicationIdOrderByPageNrAsc(page.getPublication().getId());
        Collections.sort(pages);

        for(int i = page.getPageNr()-1; i < pages.size(); i++) {
            pages.get(i).setPageNr(i+2);
        }

        pageRepository.save(pages);
        
        pageRepository.saveAndFlush(page);
        
        return pageRepository.findByPublicationIdOrderByPageNrAsc(page.getPublication().getId());
    }

    public List<Page> saveTrailingPages(List<Page> pages){
        Publication publication = pages.get(0).getPublication();
        List<Page> existingPages = pageRepository.findByPublicationIdOrderByPageNrAsc(publication.getId());
        Collections.sort(existingPages);

        for(int i = pages.get(0).getPageNr()-1; i < existingPages.size(); i++) {
            existingPages.get(i).setPageNr(i + pages.size() + 1);
        }

        pageRepository.save(existingPages);
        pageRepository.save(pages);
        
        return pageRepository.findByPublicationIdOrderByPageNrAsc(publication.getId());
    }

    public List<Page> updatePage(Page page){
        List<Page> pages = pageRepository.findByPublicationIdOrderByPageNrAsc(page.getPublication().getId());
        pages.remove(page); // Make sure we don't change the page of the one to be saved
        Collections.sort(pages);


        for(int i = 0; i < page.getPageNr()-1; i++)
            pages.get(i).setPageNr(i+1);
        for(int i = page.getPageNr()-1; i < pages.size(); i++)
            pages.get(i).setPageNr(i+2);

        pageRepository.save(pages);

        pageRepository.saveAndFlush(page);

        return pageRepository.findByPublicationIdOrderByPageNrAsc(page.getPublication().getId());
    }

	public Page updatePageMeta(Page page){
		Page existing = pageRepository.findOne(page.getId());
		
		existing.setNote(page.getNote());
		existing.setAdvertisement(page.isAdvertisement());
		existing.setWeb(page.isWeb());
		existing.setDone(page.isDone());
		existing.setArticles(page.getArticles());
		existing.setLayoutStatus(page.getLayoutStatus());
		
		return pageRepository.saveAndFlush(existing);
	}

    /**
     * Updates pages that are following each other. Undefined behavior if there are gaps!
     */
    public List<Page> updateTrailingPages(List<Page> pages) {
        Publication publication = pages.get(0).getPublication();
        List<Page> otherPages = pageRepository.findByPublicationIdOrderByPageNrAsc(publication.getId());
        otherPages.removeAll(pages);
        Collections.sort(otherPages);

        for(int i = 0; i < pages.get(0).getPageNr()-1;i++)
            otherPages.get(i).setPageNr(i+1);
        for(int i = pages.get(0).getPageNr()-1; i < otherPages.size(); i++)
            otherPages.get(i).setPageNr(i+1+pages.size());

        pageRepository.save(otherPages);
        pageRepository.save(pages);

        return pageRepository.findByPublicationIdOrderByPageNrAsc(publication.getId());        
    }

    public List<Page> deletePage(Page page){
        Publication publication = page.getPublication();
        List<Page> pages = pageRepository.findByPublicationIdOrderByPageNrAsc(publication.getId());
        Collections.sort(pages);

        for(int i = page.getPageNr()-1; i < pages.size(); i++) {
            pages.get(i).setPageNr(i);
        }

        pageRepository.save(pages);
        pageRepository.delete(page);

        return pageRepository.findByPublicationIdOrderByPageNrAsc(publication.getId());                
    }
}
