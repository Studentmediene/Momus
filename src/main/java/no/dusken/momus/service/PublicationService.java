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


import no.dusken.momus.model.Article;
import no.dusken.momus.model.Page;
import no.dusken.momus.model.Publication;
import no.dusken.momus.service.repository.ArticleRepository;
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
        List<Page> pages = pageRepository.findByPublicationId(id);
        for(Page p: pages){
            pageRepository.delete(p);
        }
        logger.info("Deleted all the pages from publication with id: " + id);
    }

    public List<Page> generateDisp(Long id){
        deletePagesInPublication(id);

        Publication publication = publicationRepository.findOne(id);
        List<Article> articles = sortArticles(articleRepository.findByPublicationId(id));

        List<Page> pages = new ArrayList<>();
        for(int i = 0; i < articles.size();i++){
            Page page = new Page();
            page.setPageNr(i + 1);
            Set<Article> pageArticles = new HashSet<>();
            pageArticles.add(articles.get(i));
            page.setArticles(pageArticles);
            page.setPublication(publication);
            pages.add(page);
        }

        publication.setPages(pages);
        Publication savedPublication = savePublication(publication);

        return pageRepository.findByPublicationId(savedPublication.getId());
    }

    private List<Article> sortArticles(List<Article> articles){
        articles = addSortField(articles);
        Collections.sort(articles);
        return articles;
    }

    private List<Article> addSortField(List<Article> articles){
        Map<String, Integer> sortPattern = new HashMap<String, Integer>();
        sortPattern.put("leder",0);
        sortPattern.put("nyhetskommentar",1);
        sortPattern.put("kulturprofil",2);
        sortPattern.put("sidensist",3);
        sortPattern.put("forbruker",4);
        sortPattern.put("nyhetssak",5);
        sortPattern.put("forskning",6);
        sortPattern.put("politisk",7);
        sortPattern.put("internasjonalt",8);
        sortPattern.put("sport",9);
        sortPattern.put("debatt",10);
        sortPattern.put("aktualitet",11);
        sortPattern.put("portrett",12);
        sortPattern.put("reportasje",13);
        sortPattern.put("kultur",14);
        sortPattern.put("musikk",15);
        sortPattern.put("anmeldelse",16);
        sortPattern.put("spit",17);

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
                if(type.equals("Portrett")){
                    article.setDispsort(sortPattern.get("portrett"));
                }else if(type.equals("Sidespor")){
                    article.setDispsort(sortPattern.get("sidespor"));
                }else if(type.equals("Aktualitet")) {
                    article.setDispsort(sortPattern.get("aktualitet"));
                }else{
                    article.setDispsort(sortPattern.get("reportasje"));
                }
            }else if(section.equals("Spit")){
                article.setDispsort(sortPattern.get("spit"));
            }else if(section.equals("Musikk")){
                article.setDispsort(sortPattern.get("musikk"));
            }else{
                article.setDispsort(sortPattern.size());
            }
        }
        return articles;
    }
}
