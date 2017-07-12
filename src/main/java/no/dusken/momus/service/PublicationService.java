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

    public Publication addPublication(Publication publication){
        Publication newPublication = publicationRepository.save(publication);
        newPublication = publicationRepository.findOne(newPublication.getId());
        int numPages = 64;
        for(int i = 0; i < numPages; i++){
            Page newPage = new Page();
            newPage.setPageNr(i + 1);
            newPage.setPublication(newPublication);
            newPage.setLayoutStatus(layoutStatusRepository.findByName("Ukjent"));
            pageRepository.save(newPage);
        }
        logger.info("Created new publication with data: {}", newPublication);

        return newPublication;
    }

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
                savedPage.setLayoutStatus(p.getLayoutStatus());
                savedPage.setWeb(p.isWeb());
                savedPage.setDone(p.isDone());
                pageRepository.save(savedPage);
            }
        }

        savedPublication = publicationRepository.save(savedPublication);

        logger.info("Updated publication {} with data: {}", publication.getName(), publication);

        return publicationRepository.findOne(savedPublication.getId());
    }

    public void deletePagesInPublication(Long id){
        List<Page> pages = pageRepository.findByPublicationId(id);
        for(Page p: pages){
            pageRepository.delete(p);
        }
        logger.info("Deleted all the pages from publication with id: " + id);
    }


    /**
     * Generates a disposition from the articles in the publication. Not used at the moment
     * @param publication The publication to generate disp for
     * @return The generated pages
     *
     *
     */
    public List<Page> generateDisp(Publication publication){
        deletePagesInPublication(publication.getId());

        List<Article> articles = sortArticles(articleRepository.findByPublicationId(publication.getId()));

        List<Page> pages = new ArrayList<>();
        for(int i = 0; i < articles.size();i++){
            Page page = new Page();
            page.setPageNr(i + 1);
            Set<Article> pageArticles = new HashSet<>();
            pageArticles.add(articles.get(i));
            page.setArticles(pageArticles);
            page.setPublication(publication);
            page.setLayoutStatus(layoutStatusRepository.findByName("Ukjent"));
            pages.add(page);
        }

        publication.setPages(pages);
        Publication savedPublication = savePublication(publication);

        return pageRepository.findByPublicationId(savedPublication.getId());
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
            colophonBuilder.append(p.getFullName()).append("\r\n");
        }

        colophonBuilder.append("\r\nFotografer:\r\n");
        for(Person p : photographers){
            colophonBuilder.append(p.getFullName()).append("\r\n");
        }

        colophonBuilder.append("\r\nIllustratører:\r\n");
        for(Person p : illustrators){
            colophonBuilder.append(p.getFullName()).append("\r\n");
        }
        return colophonBuilder.toString();
    }

    /**
     *
     * @return Returns the oldest publication that has not been released yet at the time of the date parameter
     */
    public Publication getActivePublication(Date date){
        List<Publication> publications = publicationRepository.findAllByOrderByReleaseDateDesc();
        Publication active = publications.get(0);
        for(Publication p : publications.subList(1,publications.size()-1)){
            if(p.getReleaseDate().before(date)){
                return active;
            }else{
                active = p;
            }
        }
        return active;
    }

    private List<Article> sortArticles(List<Article> articles){
        articles = addSortField(articles);
        Collections.sort(articles);
        return articles;
    }

    private List<Article> addSortField(List<Article> articles){
        Map<String, Integer> sortPattern = new HashMap<String, Integer>();
        sortPattern.put("leder",0);
        sortPattern.put("nyhetskommentar",10);
        sortPattern.put("kulturprofil",20);
        sortPattern.put("sidensist",30);
        sortPattern.put("forbruker",40);
        sortPattern.put("nyhetssak",50);
        sortPattern.put("forskning",60);
        sortPattern.put("politisk",70);
        sortPattern.put("internasjonalt",80);
        sortPattern.put("sport",90);
        sortPattern.put("debatt",100);
        sortPattern.put("aktualitet",110);
        sortPattern.put("portrett",120);
        sortPattern.put("reportasje",130);
        sortPattern.put("sidespor", 135);
        sortPattern.put("kultur",140);
        sortPattern.put("musikk",150);
        sortPattern.put("anmeldelse",160);
        sortPattern.put("spit",170);
        sortPattern.put("other", 180);

        for(Article article : articles){
            String name = article.getName();
            String section = article.getSection().getName();
            String type = "";
            if(article.getType() != null){
                type = article.getType().getName();
            }
            if(section.equals("Debatt")){
                if(type.equals("Kommentar")){
                    if(name.startsWith("Leder")) {
                        article.setDispsort(sortPattern.get("leder"));
                    }else if(name.equals("Nyhetskommentar")){
                        article.setDispsort(sortPattern.get("nyhetskommentar"));
                    }
                }else{
                    article.setDispsort(sortPattern.get("debatt"));
                }
            }else if(section.equals("Nyhet")){
                if(name.equals("Siden Sist")){
                    article.setDispsort(sortPattern.get("sidensist"));
                }else{
                    article.setDispsort(sortPattern.get("nyhetssak"));
                }
            }else if(section.equals("Kultur")){
                if(name.startsWith("Kulturprofil")){
                    article.setDispsort(sortPattern.get("kulturprofil"));
                }else if(type.equals("Anmeldelse") || name.contains("Anmeldelse")){
                    article.setDispsort(sortPattern.get("anmeldelse"));
                }else{
                    article.setDispsort(sortPattern.get("kultur"));
                }
            }else if(name.equals("Forbruker") || type.equals("Forbruker")){
                article.setDispsort(sortPattern.get("forbruker"));
            }else if(type.equals("Forskning")){
                article.setDispsort(sortPattern.get("forskning"));
            }else if(section.equals("Sport")){
                article.setDispsort(sortPattern.get("sport"));
            }else if(section.equals("Reportasje")){
                switch (type) {
                    case "Portrett":
                        article.setDispsort(sortPattern.get("portrett"));
                        break;
                    case "Sidespor":
                        article.setDispsort(sortPattern.get("sidespor"));
                        break;
                    case "Aktualitet":
                        article.setDispsort(sortPattern.get("aktualitet"));
                        break;
                    default:
                        article.setDispsort(sortPattern.get("reportasje"));
                        break;
                }
            }else if(section.equals("Spit")){
                article.setDispsort(sortPattern.get("spit"));
            }else if(section.equals("Musikk")){
                article.setDispsort(sortPattern.get("musikk"));
            }else{
                article.setDispsort(sortPattern.get("other"));
            }
            if(article.getDispsort() == null){
                article.setDispsort(sortPattern.get("other"));
            }
        }
        return articles;
    }

    public PublicationRepository getPublicationRepository() {
        return publicationRepository;
    }
}
