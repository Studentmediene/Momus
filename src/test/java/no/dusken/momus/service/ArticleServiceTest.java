/*
 * Copyright 2013 Studentmediene i Trondheim AS
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

package no.dusken.momus.service;

import no.dusken.momus.service.repository.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:testContext.xml")
public class ArticleServiceTest {

    @Autowired
    PersonRepository personRepository;

    @Value("${smmdb.key}")
    String key;

    @Before
    public void setUp() throws Exception {
        System.out.println("Setup");
        System.out.println(key);
    }

    @Test
    public void testGetArticleById() throws Exception {
        assertEquals(false, true);
    }

    @Test
    public void testSaveArticle() throws Exception {
        assertEquals(true, true);
    }

    @Test
    public void testGetAllArticles() throws Exception {
        assertEquals("lol", "laal");
    }

    @Test
    public void testSearch() throws Exception {
        assertEquals("lol", "lol");
    }

    @Test
    public void testSaveArticleContents() throws Exception {

    }
}
