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

package no.dusken.momus.service.drive;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts content from a Google Drive Document
 * to our representation
 */
@Service
public class GoogleDocsTextConverter {

    Pattern body = Pattern.compile("<body.*?>(.*)</body>");
    Pattern aTags = Pattern.compile("<a.*?></a>");
    Pattern classes = Pattern.compile(" class=\".*?\"");
    Pattern spans = Pattern.compile("</?span.*?>");
    Pattern emptyP = Pattern.compile("<p>\\s?</p>");

    Pattern inlineComments = Pattern.compile("<sup>.*?</sup>");
    Pattern spaces = Pattern.compile("&nbsp;");
    Pattern comments = Pattern.compile("<div><p>.*?</p></div>");

    Pattern lists = Pattern.compile(" start=\".*?\"");

    Pattern table = Pattern.compile("<table.*?>.*?</table>");
    Pattern img = Pattern.compile("<img.*?>");

    public String convert(String input) {
        String out = extractBody(input);


        out = removeATags(out);
        out = removeClasses(out);
        out = removeSpans(out);
        out = removeComments(out);
        out = removeInvalidContent(out);
        out = removeListAttributes(out);
        out = removeEmptyPTags(out);

        out = StringEscapeUtils.unescapeHtml4(out);

        return out;
    }

    /**
     * Only interested in the stuff inside <body></body>
     */
    private String extractBody(String in) {
        Matcher m = body.matcher(in);

        if (m.find()) {
            return m.group(1);
        }
        return in;
    }

    /**
     * Remove <a name=*></a> stuff google inserts everywhere
     */
    private String removeATags(String in) {
        Matcher m = aTags.matcher(in);
        return m.replaceAll("");
    }

    private String removeClasses(String in) {
        Matcher m = classes.matcher(in);
        return m.replaceAll("");
    }

    private String removeSpans(String in) {
        Matcher m = spans.matcher(in);
        return m.replaceAll("");
    }

    /**
     * In case someone likes to have much space between their paragraphs..
     */
    private String removeEmptyPTags(String in) {
        Matcher m = emptyP.matcher(in);
        return m.replaceAll("");
    }

    /**
     * Comments inserted should be removed as they don't belong to the text
     * A comment adds a <sup>-reference to the text, and then the comment
     * itself at the bottom
     */
    private String removeComments(String in) {
        Matcher m = inlineComments.matcher(in);
        String out = m.replaceAll("");

        // Spaces inside a marked text are written as &nbsp;
        m = spaces.matcher(out);
        out = m.replaceAll(" ");

        m = comments.matcher(out);
        out = m.replaceAll("");

        return out;
    }

    /**
     * Removes some stuff from the lists
     */
    private String removeListAttributes(String in) {
        Matcher m = lists.matcher(in);
        return m.replaceAll("");
    }

    /**
     * Removes images and tables, should possibly remove more stuff
     * but try to keep the contents, not just the formatting.
     */
    private String removeInvalidContent(String in) {
        Matcher m = table.matcher(in);
        String out = m.replaceAll("");

        m = img.matcher(out);
        out = m.replaceAll(" ");

        return out;
    }
}
