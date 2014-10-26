package no.dusken.momus.service;

import no.dusken.momus.authentication.UserLoginService;
import no.dusken.momus.model.SavedSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Service
public class SavedSearchService {

    @Autowired
    private UserLoginService userLoginService;

    @PersistenceContext
    EntityManager entityManager;

    public List<SavedSearch> getAllFromUser(){
        Long user = userLoginService.getId();
        String querystr = "select a from SavedSearch WHERE a.owner =:" + user;
        TypedQuery<SavedSearch> query = entityManager.createQuery(querystring, SavedSearch.class);



        return
    }
}
