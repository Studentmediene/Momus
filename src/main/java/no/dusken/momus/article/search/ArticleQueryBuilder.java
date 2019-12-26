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

package no.dusken.momus.article.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import no.dusken.momus.common.exceptions.RestException;
import no.dusken.momus.person.PersonRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ArticleQueryBuilder {
    public static final String baseQuery = 
        "select a from Article a " + 
        "left join a.publication publication " + 
        "left join a.section section " + 
        "left join a.type type";
    public static final String baseOrder = "order by publication.releaseDate DESC";

    @Autowired
    private PersonRepository personRepository;

    public ArticleQuery buildQuery(ArticleSearchParams search) {
        List<String> conditions = new ArrayList<>();
        String fullQuery;
        Map<String, Object> queryParams = new HashMap<>();

        if (search.getFree() != null && search.getFree().length() > 0) {
            String[] words = search.getFree().split(" ");

            for (int i = 0; i < words.length; i++) {
                List<String> freeConditions = new ArrayList<>();

                freeConditions.add("a.rawcontent like :free"+i);
                freeConditions.add("LOWER(a.comment) like :free"+i);
                freeConditions.add("LOWER(a.name) like :free"+i);                
                freeConditions.add("LOWER(a.status) like :free"+i);                
                freeConditions.add("LOWER(a.review) like :free"+i);                
                freeConditions.add("(publication is not null and LOWER(publication.name) like :free"+i+")");
                freeConditions.add("(section is not null and LOWER(section.name) like :free"+i+")");
                freeConditions.add("(type is not null and LOWER(type.name) like :free"+i+")");

                freeConditions.add("exists (select p from Person p where (p member of a.journalists or p member of a.photographers or p member of a.graphics) and LOWER(p.name) LIKE :free"+i+")");
                queryParams.put("free"+i, "%" + words[i].toLowerCase() + "%");
                conditions.add("("+StringUtils.collectionToDelimitedString(freeConditions, " OR ")+")");
            }
        }
        if (search.getStatus() != null) {
            conditions.add("a.status = :status");
            queryParams.put("status", search.getStatus());
        }
        if (search.getPersons() != null && search.getPersons().size() > 0) {
            int personCount = 0;

            for (Long person : search.getPersons()) {
                conditions.add("( :personid" + personCount + " member of a.journalists or " +
                        ":personid" + personCount + " member of a.photographers or " +
                        ":personid" + personCount + " member of a.graphics )");
                queryParams.put("personid" + personCount++, personRepository.findById(person).orElseThrow(() -> new RestException("Not found", HttpServletResponse.SC_NOT_FOUND)));
            }
        }
        if (search.getSection() != null) {
            conditions.add("section.id = :secid");
            queryParams.put("secid", search.getSection());
        }
        if (search.getPublication() != null) {
            conditions.add("publication.id = :pubid");
            queryParams.put("pubid", search.getPublication());
        }
        if (search.getReview() != null) {
            conditions.add("a.review = :review");
            queryParams.put("review", search.getReview());
        }

        conditions.add("a.archived = :arch");
        queryParams.put("arch", search.isArchived());

        String allConditions = StringUtils.collectionToDelimitedString(conditions, " AND ");

        if (allConditions.equals("")) {
            fullQuery = baseQuery;
        } else {
            fullQuery = baseQuery + " WHERE " + allConditions;
        }

        fullQuery += " " + baseOrder;

        log.debug("Search query: {}", fullQuery);

        return new ArticleQuery(fullQuery, queryParams);
    }
}
