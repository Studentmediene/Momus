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

import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.PersonRepository;
import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ArticleQueryBuilderTest extends AbstractTestRunner {

    List<Long> emptyList = Collections.emptyList();

    private Person person1;
    private Person person2;

    @Mock
    PersonRepository personRepository;

    @InjectMocks
    ArticleQueryBuilder articleQueryBuilder;

    private void initPersonMocks() {
        person1 = new Person(1L, "mts", "Mats", "Matsessen", "", "", true);
        person2 = new Person(2L, "aaa", "Kåre", "Kåressen", "", "", true);

        when(personRepository.findOne(person1.getId())).thenReturn(person1);
        when(personRepository.findOne(person2.getId())).thenReturn(person2);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testEmptyQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", null, emptyList,null, null, null, 0, 0, false);

        ArticleQuery query = articleQueryBuilder.buildQuery(params);

        String expectedQuery = ArticleQueryBuilder.baseQuery + " where a.archived = :arch " + ArticleQueryBuilder.baseOrder;

        assertEquals(expectedQuery.toLowerCase(), query.getQuery().toLowerCase());
        assertEquals(1, query.getParams().size());
    }

    @Test
    public void testNullQuery() {
        ArticleSearchParams params = new ArticleSearchParams(null, null, null,null, null, null, 0, 0, false);

        ArticleQuery query = articleQueryBuilder.buildQuery(params);
        
        String expectedQuery = ArticleQueryBuilder.baseQuery + " where a.archived = :arch " + ArticleQueryBuilder.baseOrder;
        
        assertEquals(expectedQuery.toLowerCase(), query.getQuery().toLowerCase());
        assertEquals(1, query.getParams().size());
    }

    @Test
    public void testTextQuery() {
        ArticleSearchParams params = new ArticleSearchParams("fInn meg", null, emptyList, null,null, null, 0, 0, false);

        ArticleQuery query = articleQueryBuilder.buildQuery(params);

        StringBuilder expectedQueryBuilder = new StringBuilder(ArticleQueryBuilder.baseQuery);
        expectedQueryBuilder.append(" where ");
        expectedQueryBuilder.append("(a.rawcontent like :free0 or ");
        expectedQueryBuilder.append("lower(a.comment) like :free0 or ");
        expectedQueryBuilder.append("lower(a.name) like :free0 or ");        
        expectedQueryBuilder.append("(publication is not null and lower(publication.name) like :free0) or ");
        expectedQueryBuilder.append("(status is not null and lower(status.name) like :free0) or ");
        expectedQueryBuilder.append("(section is not null and lower(section.name) like :free0) or ");
        expectedQueryBuilder.append("(type is not null and lower(type.name) like :free0) or ");
        expectedQueryBuilder.append("(review is not null and lower(review.name) like :free0) or ");
        expectedQueryBuilder.append("exists (select p from Person p where (p member of a.journalists or p member of a.photographers) and LOWER(p.fullName) LIKE :free0)) and ");
        expectedQueryBuilder.append("(a.rawcontent like :free1 or ");
        expectedQueryBuilder.append("lower(a.comment) like :free1 or ");
        expectedQueryBuilder.append("lower(a.name) like :free1 or ");  
        expectedQueryBuilder.append("(publication is not null and lower(publication.name) like :free1) or ");
        expectedQueryBuilder.append("(status is not null and lower(status.name) like :free1) or ");
        expectedQueryBuilder.append("(section is not null and lower(section.name) like :free1) or ");
        expectedQueryBuilder.append("(type is not null and lower(type.name) like :free1) or ");
        expectedQueryBuilder.append("(review is not null and lower(review.name) like :free1) or ");
        expectedQueryBuilder.append("exists (select p from Person p where (p member of a.journalists or p member of a.photographers) and LOWER(p.fullName) LIKE :free1)) and ");
        expectedQueryBuilder.append("a.archived = :arch ");
        expectedQueryBuilder.append(ArticleQueryBuilder.baseOrder);

        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("free0", "%finn%");
        expectedMap.put("free1", "%meg%");
        expectedMap.put("arch", false);

        assertEquals(expectedQueryBuilder.toString().toLowerCase(), query.getQuery().toLowerCase());
        assertEquals(expectedMap, query.getParams());
    }

    @Test
    public void testStatusQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", 1L, emptyList, null,null, null, 0, 0, false);

        ArticleQuery query = articleQueryBuilder.buildQuery(params);

        String expectedQuery = ArticleQueryBuilder.baseQuery + " where status.id = :statusid and a.archived = :arch " +  ArticleQueryBuilder.baseOrder;
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("statusid", 1L);
        expectedMap.put("arch", false);

        assertEquals(expectedQuery.toLowerCase(), query.getQuery().toLowerCase());
        assertEquals(expectedMap, query.getParams());
    }

    @Test
    public void testPersonQuery() {
        initPersonMocks();
        ArticleSearchParams params = new ArticleSearchParams("", null, Arrays.asList(1L, 2L),null, null, null, 0, 0, false);

        ArticleQuery query = articleQueryBuilder.buildQuery(params);

        String expectedQuery = ArticleQueryBuilder.baseQuery + " where ( " +
                                ":personid0 member of a.journalists or " +
                                ":personid0 member of a.photographers ) and ( " +
                                ":personid1 member of a.journalists or " +
                                ":personid1 member of a.photographers ) " +
                                "and a.archived = :arch "  + ArticleQueryBuilder.baseOrder;
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("personid0", person1);
        expectedMap.put("personid1", person2);
        expectedMap.put("arch", false);

        assertEquals(expectedQuery.toLowerCase(), query.getQuery().toLowerCase());
        assertEquals(expectedMap, query.getParams());
    }

    @Test
    public void testSectionQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", null, emptyList, null,31337L, null, 0, 0, false);

        ArticleQuery query = articleQueryBuilder.buildQuery(params);

        String expectedQuery = ArticleQueryBuilder.baseQuery + " where section.id = :secid and a.archived = :arch " + ArticleQueryBuilder.baseOrder;
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("secid", 31337L);
        expectedMap.put("arch", false);

        assertEquals(expectedQuery.toLowerCase(), query.getQuery().toLowerCase());
        assertEquals(expectedMap, query.getParams());
    }

    @Test
    public void testPublicationQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", null, emptyList,null, null, 2L, 0, 0, false);

        ArticleQuery query = articleQueryBuilder.buildQuery(params);

        String expectedQuery = ArticleQueryBuilder.baseQuery + " where publication.id = :pubid and a.archived = :arch " + ArticleQueryBuilder.baseOrder;
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("pubid", 2L);
        expectedMap.put("arch", false);

        assertEquals(expectedQuery.toLowerCase(), query.getQuery().toLowerCase());
        assertEquals(expectedMap, query.getParams());
    }

    @Test
    public void testReviewQuery() {
        ArticleSearchParams params = new ArticleSearchParams("", null, emptyList, 1L,null, null, 0, 0, false);

        ArticleQuery query = articleQueryBuilder.buildQuery(params);

        String expectedQuery = ArticleQueryBuilder.baseQuery + " where review.id = :reviewid and a.archived = :arch " + ArticleQueryBuilder.baseOrder;
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("reviewid", 1L);
        expectedMap.put("arch", false);

        assertEquals(expectedQuery.toLowerCase(), query.getQuery().toLowerCase());
        assertEquals(expectedMap, query.getParams());
    }

    @Test
    public void testCombinedQuery() {
        initPersonMocks();        
        ArticleSearchParams params = new ArticleSearchParams("kombinert test", 1L, Arrays.asList(1L, 2L),null, 31337L, 2L, 0, 0, true);

        ArticleQuery query = articleQueryBuilder.buildQuery(params);

        StringBuilder expectedQueryBuilder = new StringBuilder(ArticleQueryBuilder.baseQuery);
        expectedQueryBuilder.append(" where ");
        expectedQueryBuilder.append("(a.rawcontent like :free0 or ");
        expectedQueryBuilder.append("lower(a.comment) like :free0 or ");
        expectedQueryBuilder.append("lower(a.name) like :free0 or ");        
        expectedQueryBuilder.append("(publication is not null and lower(publication.name) like :free0) or ");
        expectedQueryBuilder.append("(status is not null and lower(status.name) like :free0) or ");
        expectedQueryBuilder.append("(section is not null and lower(section.name) like :free0) or ");
        expectedQueryBuilder.append("(type is not null and lower(type.name) like :free0) or ");
        expectedQueryBuilder.append("(review is not null and lower(review.name) like :free0) or ");
        expectedQueryBuilder.append("exists (select p from Person p where (p member of a.journalists or p member of a.photographers) and LOWER(p.fullName) LIKE :free0)) and ");
        expectedQueryBuilder.append("(a.rawcontent like :free1 or ");
        expectedQueryBuilder.append("lower(a.comment) like :free1 or ");
        expectedQueryBuilder.append("lower(a.name) like :free1 or ");  
        expectedQueryBuilder.append("(publication is not null and lower(publication.name) like :free1) or ");
        expectedQueryBuilder.append("(status is not null and lower(status.name) like :free1) or ");
        expectedQueryBuilder.append("(section is not null and lower(section.name) like :free1) or ");
        expectedQueryBuilder.append("(type is not null and lower(type.name) like :free1) or ");
        expectedQueryBuilder.append("(review is not null and lower(review.name) like :free1) or ");
        expectedQueryBuilder.append("exists (select p from Person p where (p member of a.journalists or p member of a.photographers) and LOWER(p.fullName) LIKE :free1)) and ");
        expectedQueryBuilder.append("status.id = :statusid and ");
        expectedQueryBuilder.append("( :personid0 member of a.journalists or ");
        expectedQueryBuilder.append(":personid0 member of a.photographers ) and ");
        expectedQueryBuilder.append("( :personid1 member of a.journalists or ");
        expectedQueryBuilder.append(":personid1 member of a.photographers ) and ");
        expectedQueryBuilder.append("section.id = :secid and ");
        expectedQueryBuilder.append("publication.id = :pubid and ");
        expectedQueryBuilder.append("a.archived = :arch ");
        expectedQueryBuilder.append(ArticleQueryBuilder.baseOrder);

        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("free0", "%kombinert%");
        expectedMap.put("free1", "%test%");
        expectedMap.put("statusid", 1L);
        expectedMap.put("personid0", person1);
        expectedMap.put("personid1", person2);
        expectedMap.put("secid", 31337L);
        expectedMap.put("pubid", 2L);
        expectedMap.put("arch", true);

        assertEquals(expectedQueryBuilder.toString().toLowerCase(), query.getQuery().toLowerCase());
        assertEquals(expectedMap, query.getParams());
    }
}
