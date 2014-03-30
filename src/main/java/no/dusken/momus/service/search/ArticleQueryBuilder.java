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

package no.dusken.momus.service.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleQueryBuilder {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private ArticleSearchParams search;
    private String fullQuery;
    private Map<String, Object> queryParams = new HashMap<>();

    public ArticleQueryBuilder(ArticleSearchParams search) {
        this.search = search;
        buildQuery();
    }

    private void buildQuery() {
        List<String> conditions = new ArrayList<>();

        String baseQuery = "select a from Article a";

        if (search.getFree().length() > 0) {
            conditions.add("a.content like :free");
            queryParams.put("free", "%" + search.getFree() + "%");
        }

        // todo: do it for all cases, old stuff is commented out below

        String allConditions = StringUtils.collectionToDelimitedString(conditions, " AND ");

        if (allConditions.equals("")) {
            fullQuery = baseQuery;
        } else {
            fullQuery = baseQuery + " WHERE " + allConditions;
        }

        logger.debug("Search: {}", fullQuery);

        //        String finalQuery = "SELECT DISTINCT A.ID " +
//                            "FROM ARTICLE AS A " +
//                            "LEFT JOIN 	ARTICLE_JOURNALIST AS AJ " +
//                            "ON A.ID = AJ.ARTICLE_ID " +
//                            "LEFT JOIN	ARTICLE_PHOTOGRAPHER AS AP " +
//                            "ON A.ID = AP.ARTICLE_ID " +
//                            "WHERE";
//
//        if (search.getStatus().length() > 0) {
//            finalQuery += " A.STATUS_ID = " + search.getStatus() + " AND ";
//        }
//        if (search.getPublication().length() > 0) {
//            finalQuery += " A.PUBLICATION_ID = " + search.getPublication() + " AND ";
//        }
//        if (search.getFree().length() > 0) {
//            finalQuery += " A.CONTENT LIKE '%" + search.getFree() + "%' AND ";
//        }
//        if (search.getSection().length() > 0) {
//            finalQuery += " A.TYPE_ID = " + search.getSection() + " AND ";
//        }
//        if (search.getPersons().size() > 0) {
//            for (String id : search.getPersons()) {
//                finalQuery += " (AJ.JOURNALISTS_ID = " + id + " OR AP.PHOTOGRAPHERS_ID = " + id + ")";
//                finalQuery += " AND ";
//            }
//        }

    }

    public String getFullQuery() {
        return fullQuery;
    }

    public Map<String, Object> getQueryParams() {
        return queryParams;
    }
}
