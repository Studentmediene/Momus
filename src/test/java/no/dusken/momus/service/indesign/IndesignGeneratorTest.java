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

import java.util.*;

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
        /*
        The content should test all kind of elements and some edge cases
        Now we test for:
            line endings inside a paragraph
            extra lines at the end of a tag
            a new line inside the html
         */
        Article article = Article.builder()
                .name("Min tittel")
                .content("<h1>Tittel</h1><h2>Ingress<br></h2><h3>En mellomtittel</h3><p>Et avsnitt</p>\n" +
                        "<p>Enda et avsnitt og dette avsnittet har<br>linjeskift midt inni!<br></p>" +
                        "<h3>En mellomtittel</h3><p>Med et nytt avsnitt</p><ul><li>Med en punktliste</li><li>element 2</li><li>element 3</li></ul>" +
                        "<p>et nytt avsnitt</p><ol><li>Med nummerert liste</li><li>element 2</li></ol>" +
                        "<blockquote>og så et kult sitat</blockquote>" +
                        "<p>og til slutt et avsnitt med <i>utheving</i> og <b>fet skrift</b>, kult?<br></p>")
                .journalists(new HashSet<>(Arrays.asList(
                        Person.builder().id(0L).name("Mats Matsesen").build(),
                        Person.builder().id(1L).name("Kåre Kål").build()
                )))
                .photographers(Collections.singleton(Person.builder().name("Einar Einarsen").build()))
                .publication(Publication.builder().name("TestPub").build())
                .build();

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
        Article article = Article.builder()
                .content("")
                .useIllustration(true)
                .journalists(new HashSet<>())
                .photographers(new HashSet<>(Arrays.asList(
                        Person.builder().id(0L).name("Einar Einarsen").build(),
                        Person.builder().id(1L).name("Roy Royce").build()
                )))
                .publication(Publication.builder().name("TestPub").build())
                .build();

        String expected = "<UNICODE-WIN>\r\n" +
                "<Version:7.5>\r\n" +
                "<ParaStyle:Byline>Illustrasjon: Einar Einarsen, Roy Royce\r\n";

        IndesignExport result = indesignGenerator.generateFromArticle(article);

        assertEquals(expected, result.getContent());
    }

    @Test
    public void testImageTextIsAdded() {
        Article article = Article.builder()
                .content("<h1>Min kule tittel!</h1><p>Ingen bildetekst her, plz!</p>")
                .imageText("Bilde 1 er tatt av Kåre, viser John.\nBilde med grønn fyr viser en grønn fyr.")
                .journalists(new HashSet<>())
                .photographers(Collections.singleton(Person.builder().name("Einar Einarsen").build()))
                .build();

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
        Article article = Article.builder()
                .content("<h1>Min kule tittel!</h1>")
                .journalists(new HashSet<>())
                .photographers(Collections.singleton(Person.builder().name("Einar Einarsen").build()))
                .externalAuthor("Ekstern Eksternsen")
                .externalPhotographer("Fotogjengen")
                .build();

        String expected = "<UNICODE-WIN>\r\n" +
                "<Version:7.5>\r\n" +
                "<ParaStyle:Byline>Tekst: Ekstern Eksternsen\r\n" +
                "<ParaStyle:Byline>Foto: Einar Einarsen, Fotogjengen\r\n" +
                "<ParaStyle:Tittel>Min kule tittel!\r\n";

        IndesignExport result = indesignGenerator.generateFromArticle(article);

        assertEquals(expected, result.getContent());
    }
}