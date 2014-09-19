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

package no.dusken.momus.service.indesign;

import no.dusken.momus.model.Article;
import no.dusken.momus.model.Person;
import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class IndesignGeneratorTest extends AbstractTestRunner {

    @Autowired
    IndesignGenerator indesignGenerator;

    @Test
    public void testGenerateFromArticle() throws Exception {
        Article article = new Article(1L);
        article.setContent("<h1>Tittel</h1><h2>Ingress</h2><p>Something</p>");
        article.setName("Min tittel");

        Set<Person> journalists = new HashSet<>();
        journalists.add(new Person(1L, null, null, "user1", "Mats", "Matsesen", "mats@mats.mats", "12345678", true));
        journalists.add(new Person(2L, null, null, "user1", "Kåre", "Kål", "lala@lolo.com", "12345678", true));
        article.setJournalists(journalists);

        Set<Person> photographers = new HashSet<>();
        photographers.add(new Person(3L, null, null, "user1", "Einar", "Einarsen", "einar@lala.org", "12345678", true));
        article.setPhotographers(photographers);

        String expected = "<ANSI-WIN>\r\n" +
                "<Version:6>\r\n" +
                "<ParaStyle:Tittel>Min tittel\r\n" +
                "<ParaStyle:Byline>Tekst: Mats Matsesen mats@mats.mats\r\n" +
                "<ParaStyle:Byline>Tekst: Kåre Kål lala@lolo.com\r\n" +
                "<ParaStyle:Byline>Foto: Einar Einarsen einar@lala.org\r\n";
        IndesignExport result = indesignGenerator.generateFromArticle(article);

        assertEquals(expected, result.getContent());
        assertEquals("", result.getName());
    }
}