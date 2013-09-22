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

import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.PersonRepository;
import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
public class ArticleServiceTest extends AbstractTestRunner {

    @Autowired
    PersonRepository personRepository;

    @Value("${smmdb.key}")
    String key;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetArticleById() throws Exception {
    }

    @Test
    public void testSaveArticle() throws Exception {
    }

    @Test
    public void testGetAllArticles() throws Exception {
    }

    @Test
    public void testSearch() throws Exception {
    }

    @Test
    public void testSaveArticleContents() throws Exception {

    }
}
