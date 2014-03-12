package no.dusken.momus.controller;


import no.dusken.momus.model.*;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.ArticleStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/search")
public class SearchController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleStatusRepository articleStatusRepository;

    private Search data;

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody List<Article> getSearchData(@RequestBody Search search) {
        logger.debug("getArticle_status_data");

        //Check if only free search is set
        if (isOnlyFreeSearch(search)) {
            return articleRepository.findByNameOrStatus_NameOrJournalistsOrPhotographersOrPublication_Name(search.getFree(), search.getFree(), search.getPersons(), search.getPersons(), search.getFree());
        }
        //Check if only status is set
        else if (isOnlyStatus(search)) {
            return articleRepository.findByStatus_Name(search.getStatus());
        }
        // Check if only people are set
        else if (isOnlyPersons(search)) {
            return articleRepository.findByJournalistsOrPhotographers(search.getPersons(), search.getPersons());
        }
        // Check if only section is set
//        else if ((search.getFree().length() <= 0) && (search.getStatus().length()) <= 0 && (search.getPersons().size() == 0 ) &&
//            (search.getSection().length() >= 0) && (search.getPublication().length() <= 0)) {
//            return articleRepository.findBySection(String section);
//        }
        // Check if only publication is set
        else if(isOnlyPublication(search)) {
            return articleRepository.findByPublication_Name(search.getPublication());
        }

        return articleRepository.findByNameOrStatus_NameOrJournalistsOrPhotographersOrPublication_Name(search.getFree(), search.getStatus(), search.getPersons(), search.getPersons(), search.getPublication());
    }

    public boolean isOnlyFreeSearch(Search search) {
        if ((search.getFree().length() >= 0) && (search.getStatus().length()) <= 0 && (search.getPersons().size() == 0 ) &&
                (search.getSection().length() <= 0) && (search.getPublication().length() <= 0)) {
            return true;
        }
        return false;
    }
    public boolean isOnlyStatus(Search search) {
        if ((search.getFree().length() <= 0) && (search.getStatus().length()) > 0 && (search.getPersons().size() == 0 ) &&
                (search.getSection().length() <= 0) && (search.getPublication().length() <= 0)) {
            return true;
        }
        return false;
    }
    public boolean isOnlyPersons(Search search) {
        if ((search.getFree().length() <= 0) && (search.getStatus().length()) <= 0 && (search.getPersons().size() > 0 ) &&
                (search.getSection().length() <= 0) && (search.getPublication().length() <= 0)) {
            return true;
        }
        return false;
    }
    public boolean isOnlyPublication(Search search) {
        if ((search.getFree().length() <= 0) && (search.getStatus().length()) <= 0 && (search.getPersons().size() == 0 ) &&
                (search.getSection().length() <= 0) && (search.getPublication().length() >= 0)) {
            return true;
        }
        return false;
    }
}


