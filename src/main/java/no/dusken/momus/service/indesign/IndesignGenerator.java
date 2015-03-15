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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
/**
 * A class that generates IndesignExports in their weird XML format.
 * It uses \r\n line endings because that's how InDesign wants it.
 */
public class IndesignGenerator {

    private Logger logger = LoggerFactory.getLogger(getClass());

    final Map<String, String> replacements = new LinkedHashMap<>();


    /*
    TODO: Lister, <i>, m-dash, unicode problemer? byline
     */

    public IndesignGenerator() {
        /*
        Set up all the find&replace stuff needed.
        replacements.put("a", "b") means all a's in the text will
        be replaced with b's.
         */


        // remove all new lines
        replacements.put("\n", "");

        replacements.put("<br></", "</"); // ignore line breaks at end of tags
        replacements.put("<br>", "<0x000A>"); // in-line line-breaks
        replacements.put("–", "<0x2014>"); // m-dash?
        replacements.put("—", "<0x2014>"); // m-dash

        // change paragraphs and stuff to InDesign equivalent
        replacements.put("<h1>", "<ParaStyle:Tittel>");
        replacements.put("</h1>", "\r\n");

        replacements.put("<h2>", "<ParaStyle:Stikktittel>");
        replacements.put("</h2>", "\r\n");

        replacements.put("<h3>", "<ParaStyle:Mellomtittel>");
        replacements.put("</h3>", "\r\n");

        replacements.put("<h4>", "<ParaStyle:Ingress>");
        replacements.put("</h4>", "\r\n");

        replacements.put("<p>", "<ParaStyle:Brødtekst>");
        replacements.put("</p>", "\r\n");

        replacements.put("<blockquote>", "<ParaStyle:Sitat>");
        replacements.put("</blockquote>", "\r\n");

        replacements.put("<i>", "<cTypeface:Italic>");
        replacements.put("</i>", "<cTypeface:>");

        replacements.put("<b>", "<cTypeface:Bold>");
        replacements.put("</b>", "<cTypeface:>");

        /*
            Converts:
            <ul><li>pkt1</li><li>pkt2</li><li>pkt3</li></ul>
            To:
            <bnListType:Bullet>pkt1
            pkt2
            pkt3

            and the same for <ol>, since that's the format for InDesign
         */
        replacements.put("<ul><li>", "<bnListType:Bullet>"); // special handling for first element
        replacements.put("<ol><li>", "<bnListType:Numbered>"); // the same
        replacements.put("<li>", "");
        replacements.put("</li>", "\r\n");
        replacements.put("</ul>", "");
        replacements.put("</ol>", "");


    }

    public IndesignExport generateFromArticle(Article article) {
        StringBuilder sb = new StringBuilder();

        appendHeaders(sb);
        appendByLines(sb, article);
        appendContent(sb, article);
        apppendImageText(sb, article);

        String text = sb.toString();

        String fileName = createFileName(article);

        logger.info("Generated InDesign file for article id {} with content\n{}", article.getId(), text);

        return new IndesignExport(fileName, text);
    }

    private String createFileName(Article article) {
        String fileName = "";

        if (article.getPublication() != null) {
            fileName = article.getPublication().getName() + " ";
        }

        fileName += article.getName();

        return fileName;
    }

    private void appendHeaders(StringBuilder sb) {
        sb.append("<UNICODE-WIN>\r\n<Version:7.5>\r\n");
    }

    private void appendByLines(StringBuilder sb, Article article) {
        if (article.getJournalists().size() > 0) {
            sb.append("<ParaStyle:Byline>Tekst: ");
            String delim = "";

            for (Person person : article.getJournalists()) {
                sb.append(delim).append(person.getFullName());
                delim = ", ";
            }

            sb.append("\r\n");
        }


        if (article.getPhotographers().size() > 0) {
            sb.append("<ParaStyle:Byline>");

            if (article.getUseIllustration()) {
                sb.append("Illustrasjon: ");
            } else {
                sb.append("Foto: ");
            }

            String delim = "";
            for (Person person : article.getPhotographers()) {
                sb.append(delim).append(person.getFullName());
                delim = ", ";
            }

            sb.append("\r\n");
        }


    }


    private void appendContent(StringBuilder sb, Article article) {
        String text = article.getContent();

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }

        sb.append(text);
    }

    private void apppendImageText(StringBuilder sb, Article article) {
        String text = article.getImageText();

        if (text == null || text.isEmpty()) {
            return;
        }

        text = text.replaceAll("\n", "<0x000A>");

        sb.append("<ParaStyle:Bildetekster>").append(text).append("\r\n");
    }
}
