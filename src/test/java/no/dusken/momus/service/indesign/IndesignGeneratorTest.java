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

package no.dusken.momus.service.indesign;

import no.dusken.momus.model.Article;
import no.dusken.momus.model.Person;
import no.dusken.momus.model.Publication;
import no.dusken.momus.service.AbstractServiceTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class IndesignGeneratorTest extends AbstractServiceTest {

    @InjectMocks
    private IndesignGenerator indesignGenerator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerateFromArticle() {
        Article article = Article.builder().id(1L).build();

        /*
        The content should test all kind of elements and some edge cases
        Now we test for:
            line endings inside a paragraph
            extra lines at the end of a tag
            a new line inside the html
         */

        article.setContent("<h1>Tittel</h1><h2>Ingress<br></h2><h3>En mellomtittel</h3><p>Et avsnitt</p>\n" +
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
        journalists.add(Person.builder().id(0L).name("Mats Matsesen").build());
        journalists.add(Person.builder().id(1L).name("Kåre Kål").build());
        article.setJournalists(journalists);

        Set<Person> photographers = new HashSet<>();
        photographers.add(Person.builder().name("Einar Einarsen").build());
        article.setPhotographers(photographers);

        String expected = "<UNICODE-WIN>\r\n" +
                "<Version:7.5>\r\n" +
                "<ParaStyle:Byline>Tekst: Mats Matsesen, Kåre Kål\r\n" +
                "<ParaStyle:Byline>Foto: Einar Einarsen\r\n" +
                "<ParaStyle:Tittel>Tittel\r\n" +
                "<ParaStyle:Ingress>Ingress\r\n" +
                "<ParaStyle:Mellomtittel>En mellomtittel\r\n" +
                "<ParaStyle:Brødtekst>Et avsnitt\r\n" +
                "<ParaStyle:Brødtekst>Enda et avsnitt og dette avsnittet har\r\nlinjeskift midt inni!\r\n" +
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
        Article article = Article.builder().id(1L).build();

        Publication pub = new Publication();
        pub.setName("TestPub");
        article.setPublication(pub);

        article.setContent("");

        article.setJournalists(new HashSet<>());

        Set<Person> photographers = new HashSet<>();
        photographers.add(Person.builder().id(0L).name("Einar Einarsen").build());
        photographers.add(Person.builder().id(1L).name("Roy Royce").build());
        article.setPhotographers(photographers);

        article.setUseIllustration(true);

        String expected = "<UNICODE-WIN>\r\n" +
                "<Version:7.5>\r\n" +
                "<ParaStyle:Byline>Illustrasjon: Einar Einarsen, Roy Royce\r\n";

        IndesignExport result = indesignGenerator.generateFromArticle(article);

        assertEquals(expected, result.getContent());
    }

    @Test
    public void testImageTextIsAdded() {
        Article article = Article.builder().id(1L).build();
        article.setContent("<h1>Min kule tittel!</h1><p>Ingen bildetekst her, plz!</p>");

        article.setImageText("Bilde 1 er tatt av Kåre, viser John.\nBilde med grønn fyr viser en grønn fyr.");

        article.setJournalists(new HashSet<>());
        Set<Person> photographers = new HashSet<>();
        photographers.add(Person.builder().name("Einar Einarsen").build());
        article.setPhotographers(photographers);

        String expected = "<UNICODE-WIN>\r\n" +
                "<Version:7.5>\r\n" +
                "<ParaStyle:Byline>Foto: Einar Einarsen\r\n" +
                "<ParaStyle:Tittel>Min kule tittel!\r\n" +
                "<ParaStyle:Brødtekst>Ingen bildetekst her, plz!\r\n" +
                "<ParaStyle:Bildetekster>Bilde 1 er tatt av Kåre, viser John.\r\nBilde med grønn fyr viser en grønn fyr.\r\n";

        IndesignExport result = indesignGenerator.generateFromArticle(article);

        assertEquals(expected, result.getContent());
    }



    @Test
    public void testExternalAuthors() {
        Article article = Article.builder().id(1L).build();
        article.setContent("<h1>Min kule tittel!</h1>");

        article.setJournalists(new HashSet<>());
        Set<Person> photographers = new HashSet<>();
        photographers.add(Person.builder().name("Einar Einarsen").build());
        article.setPhotographers(photographers);

        article.setExternalAuthor("Ekstern Eksternsen");
        article.setExternalPhotographer("Fotogjengen");

        String expected = "<UNICODE-WIN>\r\n" +
                "<Version:7.5>\r\n" +
                "<ParaStyle:Byline>Tekst: Ekstern Eksternsen\r\n" +
                "<ParaStyle:Byline>Foto: Einar Einarsen, Fotogjengen\r\n" +
                "<ParaStyle:Tittel>Min kule tittel!\r\n";

        IndesignExport result = indesignGenerator.generateFromArticle(article);

        assertEquals(expected, result.getContent());
    }
}