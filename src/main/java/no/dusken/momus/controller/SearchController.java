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

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Controller
@RequestMapping("/search")
public class SearchController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleStatusRepository articleStatusRepository;

    @Autowired
    private DataSource dataSource;


    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody void getSearchData(@RequestBody Search search) {
        logger.debug("getArticle_status_data");

        String query = new String("SELECT * FROM ");
        String statusQuery = null;
        String publicationQuery = null;
        String personQuery = null;

        if (search.getStatus().length() > 0) {
            query += " ARTICLESTATUS,";
            statusQuery = " NAME = '" + search.getStatus() + "'";
        }
        if (search.getPublication().length() > 0) {
            query += " PUBLICATION,";
            publicationQuery = " NAME = '" + search.getPublication() + "'";
        }
        if (search.getPersons().size() > 0) {
            query += " PERSON,";
            personQuery = " ID = ";
            for (String s : search.getPersons()) {
                personQuery += s;
            }
//            personQuery = " ID IN (";
//            for (String s : search.getPersons()) {
//                personQuery +=  s + ", ";
//            }
//            personQuery = personQuery.substring(0, personQuery.length()-2);
//            personQuery += ")";
        }

        query = query.substring(0, query.length()-1);
        query += " WHERE ";


        boolean isAdded = false;
        if (statusQuery != null) {
            query += statusQuery + " AND ";
            isAdded = true;
        }
        if (publicationQuery != null) {
            query += publicationQuery + " AND ";
            isAdded = true;
        }
        if (personQuery != null) {
            query += personQuery + " AND ";
            isAdded = true;
        }
        if (isAdded) {
            query = query.substring(0, query.length()-4);
        }

        query = query.toUpperCase();

        logger.debug(query);

        String freeQuery = "SELECT * " +
                            "FROM ARTICLESTATUS, PUBLICATION, PERSON" +
                            "WHERE = " + search.getFree();

        Connection connection = null;
        Statement statement = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String test = Integer.toString(resultSet.getInt("ID"));
                logger.debug("ID: " + test);
                String test2 = resultSet.getString("FIRSTNAME");
                logger.debug("FIRSTNAME: " + test2);
            }

        } catch (SQLException e) {
            logger.debug("SQL EXCEPTION");
            logger.debug("exception was", e);
        } catch (NullPointerException e) {
            logger.debug("nullpointer: " , e);
        }




//        //Check if only free search is set
//        if (isOnlyFreeSearch(search)) {
//            return articleRepository.findByNameOrStatus_NameOrJournalistsOrPhotographersOrPublication_Name(search.getFree(), search.getFree(), search.getPersons(), search.getPersons(), search.getFree());
//        }
        //Check if only status is set
//        if (isOnlyStatus(search)) {
//            return articleRepository.findByStatus_Name(search.getStatus());
//        }
//        // Check if only people are set
//        else if (isOnlyPersons(search)) {
//            return articleRepository.findByJournalistsOrPhotographers(search.getPersons(), search.getPersons());
//        }
//        // Check if only section is set
//        else if ((search.getFree().length() <= 0) && (search.getStatus().length()) <= 0 && (search.getPersons().size() == 0 ) &&
//            (search.getSection().length() >= 0) && (search.getPublication().length() <= 0)) {
//            return articleRepository.findBySection(String section);
//        }
//        // Check if only publication is set
//        else if(isOnlyPublication(search)) {
//            return articleRepository.findByPublication_Name(search.getPublication());
//        }

//        return articleRepository.findByNameOrStatus_NameOrJournalistsOrPhotographersOrPublication_Name(search.getFree(), search.getStatus(), search.getPersons(), search.getPersons(), search.getPublication());
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


