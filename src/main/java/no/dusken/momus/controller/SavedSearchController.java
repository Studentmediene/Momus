package no.dusken.momus.controller;

import no.dusken.momus.model.SavedSearch;
import no.dusken.momus.service.SavedSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/savedsearches")
public class SavedSearchController {
    @Autowired
    SavedSearchService savedSearchService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public @ResponseBody List<SavedSearch> getSavedSearches(){
        return savedSearchService.getAllFromUser();
    }
}
