package no.dusken.momus.service.chimera;

import no.dusken.momus.model.Article;
import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
                "<p><i>Dette er italics</i></p>");


        assertEquals("#Dette er tittelen\n####Dette er en ingress\nDette er et avsnitt som bare er en test.\n* Dette er et element\n* Et til element\n* Enda et\n1. Første\n2. Andre\n3. Tredje\n**Dette er fet skrift**\n\n*Dette er italics*\n",chimeraExport.htmlToMarkdownNew(article.getContent()));
    }
}
