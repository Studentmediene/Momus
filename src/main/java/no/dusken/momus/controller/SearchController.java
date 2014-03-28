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
    private DataSource dataSource;


    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody List<Article> getSearchData(@RequestBody Search search) {
        String finalQuery = "SELECT DISTINCT A.ID " +
                            "FROM ARTICLE AS A " +
                            "FULL JOIN 	ARTICLE_JOURNALIST AS AJ ON A.ID = AJ.ARTICLE_ID "+
                            "FULL JOIN	ARTICLE_PHOTOGRAPHER AS AP ON A.ID = AP.ARTICLE_ID "+
                            "WHERE";

        if (search.getStatus().length() > 0) {
            finalQuery += " A.STATUS_ID = " + search.getStatus() + " AND ";
        }
        if (search.getPublication().length() > 0) {
            finalQuery += " A.PUBLICATION_ID = " + search.getPublication() + " AND ";
        }
        if (search.getFree().length() > 0) {
            finalQuery += " A.CONTENT LIKE '%" + search.getFree() + "%' AND ";
        }
        if (search.getPersons().size() > 0) {
            for (String id : search.getPersons()) {
                finalQuery += " (AJ.JOURNALISTS_ID = " + id + " OR AP.PHOTOGRAPHERS_ID = " + id + ")";
                finalQuery += " AND ";
            }

        }
        if (finalQuery.endsWith("WHERE")) {
            logger.debug("Nothing was asked for");
            return new ArrayList<Article>();
        }
        else {
            finalQuery = finalQuery.substring(0, finalQuery.length()-4);
            finalQuery += ";";
        }
        logger.debug("finalQuery: " +  finalQuery);
        Connection connection;
        Statement statement;
        Set<Long> articleIDs = new HashSet<>();

        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(finalQuery);
            logger.debug("HEIHEIHE");

            //Need to have this if statement if the query returns zero results. Don't ask me why..
            if (resultSet.next()) {
                Long id = resultSet.getLong(1); // Fetching id from the first column in the first row of the table
                logger.debug("ID : " + id);
                articleIDs.add(id);
            }
            else
                return new ArrayList<>();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1); //Fetching id from the first column in second row and so forth in the table
                logger.debug("ID: " + id);
                articleIDs.add(id);
            }
            statement.close();
        }
        catch (SQLException e) {
            logger.debug("SQL EXCEPTION");
            logger.debug("exception was", e);
            return new ArrayList<>();
        } catch (NullPointerException e) {
            logger.debug("nullpointer: " , e);
            return new ArrayList<>();
        }

        return articleRepository.findByIdIn(articleIDs);

    }
}