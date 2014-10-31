package no.dusken.momus.controller;

import no.dusken.momus.model.SavedSearch;
import no.dusken.momus.service.SavedSearchService;
import no.dusken.momus.service.search.ArticleSearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public @ResponseBody SavedSearch saveSearch(@RequestBody SavedSearch savedSearch){
        return savedSearchService.saveNewSearch(savedSearch);
    }

    @RequestMapping(value = "/del/{id}", method = RequestMethod.DELETE)
    public @ResponseBody String deleteSearch(@PathVariable("id") Long id){
        return savedSearchService.deleteSearch(id);
    }
}
