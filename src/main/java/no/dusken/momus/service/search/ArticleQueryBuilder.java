/*
 * Copyright 2016 Studentmediene i Trondheim AS
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

    private final String baseQuery = "select a from Article a left join fetch a.status left join fetch a.publication";
    private final String baseOrder = "order by a.publication.releaseDate DESC";

    public ArticleQueryBuilder(ArticleSearchParams search) {
        this.search = search;
        buildQuery();
    }

    private void buildQuery() {
        List<String> conditions = new ArrayList<>();

        if (search.getFree() != null && search.getFree().length() > 0) {
            String[] words = search.getFree().split(" ");

            for (int i = 0; i < words.length; i++) {
                conditions.add("a.rawcontent like :free"+i);
                queryParams.put("free"+i, "%" + words[i].toLowerCase() + "%");
            }
        }
        if (search.getStatus() != null) {
            conditions.add("a.status.id = :statusid");
            queryParams.put("statusid", search.getStatus());
        }
        if (search.getPersons() != null && search.getPersons().size() > 0) {
            int personCount = 0;

            for (Long person : search.getPersons()) {
                conditions.add("( :personid" + personCount + " member of a.journalists or " +
                        ":personid" + personCount + " member of a.photographers )");
                queryParams.put("personid" + personCount++, person);
            }
        }
        if (search.getSection() != null) {
            conditions.add("a.section.id = :secid");
            // TODO fix to use section!!
            queryParams.put("secid", search.getSection());
        }
        if (search.getPublication() != null) {
            conditions.add("a.publication.id = :pubid");
            queryParams.put("pubid", search.getPublication());
        }
        if (search.getReview() != null) {
            conditions.add("a.review.id = :reviewid");
            queryParams.put("reviewid", search.getReview());
        }

        conditions.add("a.archived = :arch");
        queryParams.put("arch", search.getArchived());

        String allConditions = StringUtils.collectionToDelimitedString(conditions, " AND ");

        if (allConditions.equals("")) {
            fullQuery = baseQuery;
        } else {
            fullQuery = baseQuery + " WHERE " + allConditions;
        }

        fullQuery += " " + baseOrder;

        logger.debug("Search query: {}", fullQuery);

    }

    public String getBaseQuery() {
        return baseQuery;
    }

    public String getBaseOrder() {
        return baseOrder;
    }

    public String getFullQuery() {
        return fullQuery;
    }

    public Map<String, Object> getQueryParams() {
        return queryParams;
    }
}
