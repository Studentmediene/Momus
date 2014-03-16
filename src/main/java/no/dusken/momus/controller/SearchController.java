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

        //Status and publication search query:
        String statusAndPublicationQuery = getStatusAndPublicationQuery(search.getStatus(), search.getPublication());
        //Person search query:
        String personQuery = getPersonQuery(search.getPersons());
        //Free search query:
        //TODO: Make this query really good for the free search!
        String freeQuery = getFreeQuery(search.getFree());

        if (statusAndPublicationQuery != null) {
            logger.debug("Status and pub: " + statusAndPublicationQuery);
        }
        if (personQuery != null) {
            logger.debug("Person query: " + personQuery);
        }
        if (freeQuery != null) {
            logger.debug("Free query: " + freeQuery);
        }

        Connection connection = null;
        Statement statement = null;

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet statusAndPubSet = statement.executeQuery(statusAndPublicationQuery);
            ResultSet personSet = statement.executeQuery(personQuery);
//            ResultSet freeSet = statement.executeQuery(freeQuery);

            while (statusAndPubSet.next()) {
                String test = Integer.toString(statusAndPubSet.getInt("ID"));
                logger.debug("Article of status and pub ID: " + test);
//                String test2 = statusAndPubSet.getString("NAME");
//                logger.debug("NAME OF THE ARTICLE: " + test2);
            }
            while (personSet.next()) {
                String test = Integer.toString(personSet.getInt("ARTICLE_ID"));
                logger.debug("Article by persons ID: " + test);
            }
//            while (freeSet.next()) {
//                String test = Integer.toString(freeSet.getInt("ID"));
//                logger.debug("Article from free search ID: " + test);
//            }

        } catch (SQLException e) {
            logger.debug("SQL EXCEPTION");
            logger.debug("exception was", e);
        } catch (NullPointerException e) {
            logger.debug("nullpointer: " , e);
        }

    }

    private String getFreeQuery(String free) {
        if (free.length() > 0) {

            String freeQuery = "SELECT A.ID FROM ARTICLE AS A " +
                                "WHERE ((A.NAME = '" + free + "') OR (A.ID = " + free +
                                ") OR  (A.STATUS_ID = " + free + ") OR (A.PUBLICATION_ID = " + free + "));";
            return freeQuery;
        }
        return null;
    }

    private String getPersonQuery(Set<String> persons) {
        String personQuery = "SELECT DISTINCT AJ.ARTICLE_ID, AP.ARTICLE_ID " +
                "FROM ARTICLE_JOURNALIST AS AJ, ARTICLE_PHOTOGRAPHER AS AP WHERE ";
        if (persons.size() > 0) {
            for (String id : persons) {
                personQuery += "((AJ.JOURNALISTS_ID = " + id + ") OR (AP.PHOTOGRAPHERS_ID = " + id + "))";
                personQuery += " AND ";
            }
            personQuery = personQuery.substring(0, personQuery.length()-4);
            personQuery += ";";
            return personQuery;
        }
        return null;
    }

    private String getStatusAndPublicationQuery(String status, String publication) {

        String statusAndPublicationQuery = new String("SELECT A.ID FROM ARTICLE AS A WHERE ");
        String statusQuery = null;
        String publicationQuery = null;

        if (status.length() > 0) {
            statusQuery = " A.STATUS_ID  = " + status ;
        }
        if (publication.length() > 0) {
            publicationQuery = " A.PUBLICATION_ID = " + publication;
        }

        boolean isAdded = false;
        if (statusQuery != null) {
            statusAndPublicationQuery += statusQuery + " AND ";
            isAdded = true;
        }
        if (publicationQuery != null) {
            statusAndPublicationQuery += publicationQuery + " AND ";
            isAdded = true;
        }
        if (isAdded) {
            statusAndPublicationQuery = statusAndPublicationQuery.substring(0, statusAndPublicationQuery.length()-4);
            statusAndPublicationQuery += ";";
        } else
            return null;

        return statusAndPublicationQuery;
    }
}


