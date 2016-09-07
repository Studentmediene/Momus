package no.dusken.momus.service.chimera;

import no.dusken.momus.model.Article;
import no.dusken.momus.model.Person;
import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@Transactional
public class ChimeraExportTest extends AbstractTestRunner{

    @Autowired
    ChimeraExport chimeraExport;

    @Test
    public void testMarkdownConversion() throws Exception{
        Article article = new Article((long) 0);
        article.setContent("<h1>Dette er tittelen</h1>" +
                "<h4>Dette er en ingress</h4>" +
                "<p>Dette er et avsnitt som bare er en test.</p>" +
                "<ul><li>Dette er et element</li>" +
                "<li>Et til element</li>" +
                "<li>Enda et</li></ul>" +
                "<ol><li>Første</li>" +
                "<li>Andre</li>" +
                "<li>Tredje</li></ol>" +
                "<p><b>Dette er fet skrift</b></p><br>" +
                "<p><i>Dette er italics</i></p>" +
                "<blockquote>Dette er et sitat!</blockquote>");


        assertEquals("\n#Dette er tittelen\n\n####Dette er en ingress\n\nDette er et avsnitt som bare er en test.\n\n* Dette er et element\n* Et til element\n* Enda et\n\n1. Første\n2. Andre\n3. Tredje\n\n**Dette er fet skrift**\n\n\n*Dette er italics*\n\n>Dette er et sitat!\n",chimeraExport.htmlToMarkdown(article.getContent()));
    }

    @Test
    public void testJsonConversion() throws Exception{
        Article article = new Article((long) 0);
        article.setContent("<h1>Tittel</h1><h4>Dette er en ingress</h4><p>Dette er et avsnitt</p>");
        Person p1 = new Person((long) 0);
        p1.setUsername("eivind");
        p1.setFirstName("Eivind");
        p1.setFullName("Eivind Grimstad");
        p1.setEmail("e@post.no");
        Person p2 = new Person((long) 1);
        p2.setUsername("ola");
        p2.setFirstName("Ola");
        p2.setFullName("Ola Nordmann");
        p2.setEmail("mail@mail.no");
        Set<Person> journ = new HashSet<>();
        journ.add(p1);
        journ.add(p2);
        article.setJournalists(journ);
        assertEquals("{\"headline\":\"Tittel\",\"lead\":\"Dette er en ingress\",\"body\":\"<p>Dette er et avsnitt</p>\",\"external_authors\":\"Eivind Grimstad, Ola Nordmann\"}", chimeraExport.articleToJson(article));
    }
}
