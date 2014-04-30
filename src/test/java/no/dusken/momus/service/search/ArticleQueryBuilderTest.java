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

import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class ArticleQueryBuilderTest extends AbstractTestRunner {

    Set<String> emptySet = Collections.emptySet();

    @Test
    public void testEmptyQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", "", emptySet, "", "");

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        String expectedQuery = "select a from article a";

        assertEquals(expectedQuery, builder.getFullQuery().toLowerCase());
        assertEquals(0, builder.getQueryParams().size());

    }

    @Test
    public void testTextQuery() {
        ArticleSearchParams params = new ArticleSearchParams("finn meg", "", emptySet, "", "");

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        String expectedQuery = "select a from article a where a.content like :free";
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("free", "%finn meg%");

        assertEquals(expectedQuery, builder.getFullQuery().toLowerCase());
        assertEquals(expectedMap, builder.getQueryParams());
    }

    @Test
    public void testStatusQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", "1", emptySet, "", "");

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        String expectedQuery = "select a from article a where a.status.id = :statusid";
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("statusid", 1L);

        assertEquals(expectedQuery, builder.getFullQuery().toLowerCase());
        assertEquals(expectedMap, builder.getQueryParams());
    }


    @Test
    public void testPersonQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", "", new HashSet<>(Arrays.asList("594", "1337")), "", "");

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        String expectedQuery = "select a from article a where " +
                                ":personid594 member of a.journalists or " +
                                ":personid594 member of a.photographers and " +
                                ":personid1337 member of a.journalists or " +
                                ":personid1337 member of a.photographers";
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("personid594", "594");
        expectedMap.put("personid1337", "1337");

        assertEquals(expectedQuery, builder.getFullQuery().toLowerCase());
        assertEquals(expectedMap, builder.getQueryParams());
    }

    @Test
    public void testSectionQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", "", emptySet, "31337", "");

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        String expectedQuery = "select a from article a where a.type.id = :secid";
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("secid", 31337L);

        assertEquals(expectedQuery, builder.getFullQuery().toLowerCase());
        assertEquals(expectedMap, builder.getQueryParams());
    }

    @Test
    public void testPublicationQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", "", emptySet, "", "2");

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        String expectedQuery = "select a from article a where a.publication.id = :pubid";
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("pubid", 2L);

        assertEquals(expectedQuery, builder.getFullQuery().toLowerCase());
        assertEquals(expectedMap, builder.getQueryParams());
    }

    @Test
    public void testCombinedQuery() {
        ArticleSearchParams params = new ArticleSearchParams("kombinert test", "1", new HashSet<>(Arrays.asList("594", "1337")), "31337", "2");

        ArticleQueryBuilder builder = new ArticleQueryBuilder(params);

        String expectedQuery = "select a from article a where " +
                                "a.content like :free and " +
                                "a.status.id = :statusid and " +
                                ":personid594 member of a.journalists or " +
                                ":personid594 member of a.photographers and " +
                                ":personid1337 member of a.journalists or " +
                                ":personid1337 member of a.photographers and " +
                                "a.type.id = :secid and " +
                                "a.publication.id = :pubid";

        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("free", "%kombinert test%");
        expectedMap.put("statusid", 1L);
        expectedMap.put("personid594", "594");
        expectedMap.put("personid1337", "1337");
        expectedMap.put("secid", 31337L);
        expectedMap.put("pubid", 2L);

        assertEquals(expectedQuery, builder.getFullQuery().toLowerCase());
        assertEquals(expectedMap, builder.getQueryParams());
    }

}
