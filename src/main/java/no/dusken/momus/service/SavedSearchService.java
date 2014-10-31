package no.dusken.momus.service;

import no.dusken.momus.authentication.UserLoginService;
import no.dusken.momus.model.Person;
import no.dusken.momus.model.SavedSearch;
import no.dusken.momus.service.repository.SavedSearchRepository;
import no.dusken.momus.service.search.ArticleSearchParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Service
public class SavedSearchService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    SavedSearchRepository savedSearchRepository;

    public List<SavedSearch> getAllFromUser(){
        Long user = userLoginService.getId();
        return savedSearchRepository.findByOwner_Id(user);
    }

    public SavedSearch saveNewSearch(SavedSearch savedSearch){
        Person user = userLoginService.getLoggedInUser();
        savedSearch.setOwner(user);
        Long newId = savedSearchRepository.saveAndFlush(savedSearch).getId();
        return savedSearchRepository.findOne(newId);
    }

    public String deleteSearch(Long id){
        savedSearchRepository.delete(id);
        return "Deleted";
    }
}
