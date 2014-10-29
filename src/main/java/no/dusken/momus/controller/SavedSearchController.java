package no.dusken.momus.controller;

import no.dusken.momus.model.SavedSearch;
import no.dusken.momus.service.SavedSearchService;
import no.dusken.momus.service.search.ArticleSearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/savedsearch")
public class SavedSearchController {
    @Autowired
    SavedSearchService savedSearchService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public @ResponseBody List<SavedSearch> getSavedSearches(){
        return savedSearchService.getAllFromUser();
    }

    @RequestMapping(value = "/put", method = RequestMethod.POST)
    public @ResponseBody SavedSearch saveSearch(@RequestBody SavedSearch savedSearch){
        System.out.println("hei");
        return savedSearchService.saveNewSearch(savedSearch);
    }
}
