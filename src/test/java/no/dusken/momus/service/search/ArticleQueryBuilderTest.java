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

import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class ArticleQueryBuilderTest extends AbstractTestRunner {

    List<Long> emptyList = Collections.emptyList();

    /*

    @Test
    public void testEmptyQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", null, emptyList,null, null, null, 0, 0, false);

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        String expectedQuery = builder.getBaseQuery() + " where a.archived = :arch " + builder.getBaseOrder();

        assertEquals(expectedQuery.toLowerCase(), builder.getFullQuery().toLowerCase());
        assertEquals(1, builder.getQueryParams().size());

    }

    @Test
    public void testNullQuery() {
        ArticleSearchParams params = new ArticleSearchParams(null, null, null,null, null, null, 0, 0, false);

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        String expectedQuery = builder.getBaseQuery()  + " where a.archived = :arch " + builder.getBaseOrder();

        assertEquals(expectedQuery.toLowerCase(), builder.getFullQuery().toLowerCase());
        assertEquals(1, builder.getQueryParams().size());

    }

    @Test
    public void testTextQuery() {
        ArticleSearchParams params = new ArticleSearchParams("fInn meg", null, emptyList, null,null, null, 0, 0, false);

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        StringBuilder expectedQueryBuilder = new StringBuilder(builder.getBaseQuery());
        expectedQueryBuilder.append(" where ");
        expectedQueryBuilder.append("(a.rawcontent like :free0 or ");
        expectedQueryBuilder.append("lower(a.publication.name) like :free0 or ");
        expectedQueryBuilder.append("lower(a.status.name) like :free0 or ");
        expectedQueryBuilder.append("lower(a.section.name) like :free0 or ");
        expectedQueryBuilder.append("lower(a.comment) like :free0 or ");
        expectedQueryBuilder.append("lower(a.type.name) like :free0 or ");
        expectedQueryBuilder.append("exists (select p from Person p where (p member of a.journalists or p member of a.photographers) and LOWER(p.fullName) LIKE :free0)) and ");
        expectedQueryBuilder.append("(a.rawcontent like :free1 or ");
        expectedQueryBuilder.append("lower(a.publication.name) like :free1 or ");
        expectedQueryBuilder.append("lower(a.status.name) like :free1 or ");
        expectedQueryBuilder.append("lower(a.section.name) like :free1 or ");
        expectedQueryBuilder.append("lower(a.comment) like :free1 or ");
        expectedQueryBuilder.append("lower(a.type.name) like :free1 or ");
        expectedQueryBuilder.append("exists (select p from Person p where (p member of a.journalists or p member of a.photographers) and LOWER(p.fullName) LIKE :free1)) and ");
        expectedQueryBuilder.append("a.archived = :arch ");
        expectedQueryBuilder.append(builder.getBaseOrder());

        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("free0", "%finn%");
        expectedMap.put("free1", "%meg%");
        expectedMap.put("arch", false);

        assertEquals(expectedQueryBuilder.toString().toLowerCase(), builder.getFullQuery().toLowerCase());
        assertEquals(expectedMap, builder.getQueryParams());
    }

    @Test
    public void testStatusQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", 1L, emptyList, null,null, null, 0, 0, false);

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        String expectedQuery = builder.getBaseQuery() + " where a.status.id = :statusid and a.archived = :arch " + builder.getBaseOrder();
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("statusid", 1L);
        expectedMap.put("arch", false);

        assertEquals(expectedQuery.toLowerCase(), builder.getFullQuery().toLowerCase());
        assertEquals(expectedMap, builder.getQueryParams());
    }


    @Test
    public void testPersonQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", null, Arrays.asList(594L, 1337L),null, null, null, 0, 0, false);

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        String expectedQuery = builder.getBaseQuery() + " where ( " +
                                ":personid0 member of a.journalists or " +
                                ":personid0 member of a.photographers ) and ( " +
                                ":personid1 member of a.journalists or " +
                                ":personid1 member of a.photographers ) " +
                                "and a.archived = :arch "  + builder.getBaseOrder();
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("personid0", 594L);
        expectedMap.put("personid1", 1337L);
        expectedMap.put("arch", false);

        assertEquals(expectedQuery.toLowerCase(), builder.getFullQuery().toLowerCase());
        assertEquals(expectedMap, builder.getQueryParams());
    }

    @Test
    public void testSectionQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", null, emptyList, null,31337L, null, 0, 0, false);

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        String expectedQuery = builder.getBaseQuery() + " where a.section.id = :secid and a.archived = :arch " + builder.getBaseOrder();
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("secid", 31337L);
        expectedMap.put("arch", false);

        assertEquals(expectedQuery.toLowerCase(), builder.getFullQuery().toLowerCase());
        assertEquals(expectedMap, builder.getQueryParams());
    }

    @Test
    public void testPublicationQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", null, emptyList,null, null, 2L, 0, 0, false);

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        String expectedQuery = builder.getBaseQuery() + " where a.publication.id = :pubid and a.archived = :arch " + builder.getBaseOrder();
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("pubid", 2L);
        expectedMap.put("arch", false);

        assertEquals(expectedQuery.toLowerCase(), builder.getFullQuery().toLowerCase());
        assertEquals(expectedMap, builder.getQueryParams());
    }

    @Test
    public void testReviewQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", null, emptyList, 1L,null, null, 0, 0, false);

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        String expectedQuery = builder.getBaseQuery() + " where a.review.id = :reviewid and a.archived = :arch " + builder.getBaseOrder();
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("reviewid", 1L);
        expectedMap.put("arch", false);

        assertEquals(expectedQuery.toLowerCase(), builder.getFullQuery().toLowerCase());
        assertEquals(expectedMap, builder.getQueryParams());
    }

    @Test
    public void testCombinedQuery() {
        ArticleSearchParams params = new ArticleSearchParams("kombinert test", 1L, Arrays.asList(594L, 1337L),null, 31337L, 2L, 0, 0, true);

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        StringBuilder expectedQueryBuilder = new StringBuilder(builder.getBaseQuery());
        expectedQueryBuilder.append(" where ");
        expectedQueryBuilder.append("(a.rawcontent like :free0 or ");
        expectedQueryBuilder.append("lower(a.publication.name) like :free0 or ");
        expectedQueryBuilder.append("lower(a.status.name) like :free0 or ");
        expectedQueryBuilder.append("lower(a.section.name) like :free0 or ");
        expectedQueryBuilder.append("lower(a.comment) like :free0 or ");
        expectedQueryBuilder.append("lower(a.type.name) like :free0 or ");
        expectedQueryBuilder.append("exists (select p from Person p where (p member of a.journalists or p member of a.photographers) and LOWER(p.fullName) LIKE :free0)) and ");
        expectedQueryBuilder.append("(a.rawcontent like :free1 or ");
        expectedQueryBuilder.append("lower(a.publication.name) like :free1 or ");
        expectedQueryBuilder.append("lower(a.status.name) like :free1 or ");
        expectedQueryBuilder.append("lower(a.section.name) like :free1 or ");
        expectedQueryBuilder.append("lower(a.comment) like :free1 or ");
        expectedQueryBuilder.append("lower(a.type.name) like :free1 or ");
        expectedQueryBuilder.append("exists (select p from Person p where (p member of a.journalists or p member of a.photographers) and LOWER(p.fullName) LIKE :free1)) and ");
        expectedQueryBuilder.append("a.status.id = :statusid and ");
        expectedQueryBuilder.append("( :personid0 member of a.journalists or ");
        expectedQueryBuilder.append(":personid0 member of a.photographers ) and ");
        expectedQueryBuilder.append("( :personid1 member of a.journalists or ");
        expectedQueryBuilder.append(":personid1 member of a.photographers ) and ");
        expectedQueryBuilder.append("a.section.id = :secid and ");
        expectedQueryBuilder.append("a.publication.id = :pubid and ");
        expectedQueryBuilder.append("a.archived = :arch ");
        expectedQueryBuilder.append(builder.getBaseOrder());

        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("free0", "%kombinert%");
        expectedMap.put("free1", "%test%");
        expectedMap.put("statusid", 1L);
        expectedMap.put("personid0", 594L);
        expectedMap.put("personid1", 1337L);
        expectedMap.put("secid", 31337L);
        expectedMap.put("pubid", 2L);
        expectedMap.put("arch", true);

        assertEquals(expectedQueryBuilder.toString().toLowerCase(), builder.getFullQuery().toLowerCase());
        assertEquals(expectedMap, builder.getQueryParams());
    }

    */

}
