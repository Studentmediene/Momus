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
import no.dusken.momus.model.Publication;
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

        /*
        The content should test all kind of elements and some edge cases
        Now we test for:
            line endings inside a paragraph
            extra lines at the end of a tag
            a new line inside the html
         */

        article.setContent("<h1>Tittel</h1><h2>Stikktittel<br></h2><h4>En lang ingress</h4><p>Et avsnitt</p>\n" +
                "<p>Enda et avsnitt og dette avsnittet har<br>linjeskift midt inni!<br></p>" +
                "<h3>En mellomtittel</h3><p>Med et nytt avsnitt</p><ul><li>Med en punktliste</li><li>element 2</li><li>element 3</li></ul>" +
                "<p>et nytt avsnitt</p><ol><li>Med nummerert liste</li><li>element 2</li></ol>" +
                "<blockquote>og så et kult sitat</blockquote>" +
                "<p>og til slutt et avsnitt med <i>utheving</i> og <b>fet skrift</b>, kult?<br></p>");
        article.setName("Min tittel");

        Publication pub = new Publication();
        pub.setName("TestPub");
        article.setPublication(pub);

        Set<Person> journalists = new HashSet<>();
        journalists.add(new Person(1L, "user1", "Mats", "Mats Matsesen", "mats@mats.mats", "12345678", true));
        journalists.add(new Person(2L, "user1", "Kåre", "Kåre Kål", "lala@lolo.com", "12345678", true));
        article.setJournalists(journalists);

        Set<Person> photographers = new HashSet<>();
        photographers.add(new Person(3L, "user1", "Einar", "Einar Einarsen", "einar@lala.org", "12345678", true));
        article.setPhotographers(photographers);

        String expected = "<ANSI-WIN>\r\n" +
                "<Version:7.5>\r\n" +
                "<ParaStyle:Byline>Tekst: Mats Matsesen, Kåre Kål\r\n" +
                "<ParaStyle:Byline>Foto: Einar Einarsen\r\n" +
                "<ParaStyle:Tittel>Tittel\r\n" +
                "<ParaStyle:Stikktittel>Stikktittel\r\n" +
                "<ParaStyle:Ingress>En lang ingress\r\n" +
                "<ParaStyle:Brødtekst>Et avsnitt\r\n" +
                "<ParaStyle:Brødtekst>Enda et avsnitt og dette avsnittet har<0x000A>linjeskift midt inni!\r\n" +
                "<ParaStyle:Mellomtittel>En mellomtittel\r\n" +
                "<ParaStyle:Brødtekst>Med et nytt avsnitt\r\n" +
                "<bnListType:Bullet>Med en punktliste\r\n" +
                "element 2\r\n" +
                "element 3\r\n" +
                "<ParaStyle:Brødtekst>et nytt avsnitt\r\n" +
                "<bnListType:Numbered>Med nummerert liste\r\n" +
                "element 2\r\n" +
                "<ParaStyle:Sitat>og så et kult sitat\r\n" +
                "<ParaStyle:Brødtekst>og til slutt et avsnitt med <cTypeface:Italic>utheving<cTypeface:> og <cTypeface:Bold>fet skrift<cTypeface:>, kult?\r\n"
                ;
        IndesignExport result = indesignGenerator.generateFromArticle(article);

        assertEquals(expected, result.getContent());
        assertEquals("TestPub Min tittel", result.getName());
    }

    @Test
    public void changesToIllustratorNoJournalists() {
        Article article = new Article(1L);


        Publication pub = new Publication();
        pub.setName("TestPub");
        article.setPublication(pub);

        article.setContent("");

        article.setJournalists(new HashSet<Person>());

        Set<Person> photographers = new HashSet<>();
        photographers.add(new Person(3L, "user1", "Einar", "Einar Einarsen", "einar@lala.org", "12345678", true));
        photographers.add(new Person(10L, "user1", "Roy", "Roy Royce", "einar@lala.org", "12345678", true));
        article.setPhotographers(photographers);

        article.setUseIllustration(true);

        String expected = "<ANSI-WIN>\r\n" +
                "<Version:7.5>\r\n" +
                "<ParaStyle:Byline>Illustrasjon: Einar Einarsen, Roy Royce\r\n";

        IndesignExport result = indesignGenerator.generateFromArticle(article);

        assertEquals(expected, result.getContent());
    }

    @Test
    public void testImageTextIsAdded() {
        Article article = new Article(1L);
        article.setContent("<h1>Min kule tittel!</h1><p>Ingen bildetekst her, plz!</p>");

        article.setImageText("Bilde 1 er tatt av Kåre, viser John.\nBilde med grønn fyr viser en grønn fyr.");

        article.setJournalists(new HashSet<Person>());
        Set<Person> photographers = new HashSet<>();
        photographers.add(new Person(3L, "user1", "Einar", "Einar Einarsen", "einar@lala.org", "12345678", true));
        article.setPhotographers(photographers);

        String expected = "<ANSI-WIN>\r\n" +
                "<Version:7.5>\r\n" +
                "<ParaStyle:Byline>Foto: Einar Einarsen\r\n" +
                "<ParaStyle:Tittel>Min kule tittel!\r\n" +
                "<ParaStyle:Brødtekst>Ingen bildetekst her, plz!\r\n" +
                "<ParaStyle:Bildetekster>Bilde 1 er tatt av Kåre, viser John.<0x000A>Bilde med grønn fyr viser en grønn fyr.\r\n";

        IndesignExport result = indesignGenerator.generateFromArticle(article);

        assertEquals(expected, result.getContent());
    }
}