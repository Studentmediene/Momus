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

package no.dusken.momus.article.gdocs;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts content from a Google Drive Document
 * to our representation
 */
@Service
public class GoogleDocsMarkupConverter {

    Pattern body = Pattern.compile("<body.*?>(.*)</body>");
    Pattern css = Pattern.compile("<style type=\"text/css\">(.*)</style>");
    Pattern italicStyleName = Pattern.compile("\\.([A-Za-z][^{]*?)\\{[^}]*font-style:italic[^}]*}");
    Pattern boldStyleName = Pattern.compile("\\.([A-Za-z][^{]*?)\\{[^}]*font-weight:700[^}]*}");


    Pattern aTags = Pattern.compile("<a[^>]*?></a>");
    Pattern classes = Pattern.compile(" class=\".*?\"");
    Pattern spans = Pattern.compile("</?span.*?>");
    Pattern emptyP = Pattern.compile("<p>\\s?</p>");

    ArrayList<Pattern> hTags = initTitles();

    Pattern dashes = Pattern.compile("--");
    Pattern inlineComments = Pattern.compile("<sup>.*?</sup>");
    Pattern spaces = Pattern.compile("&nbsp;");
    Pattern comments = Pattern.compile("<div><p>.*?</p></div>");

    Pattern lists = Pattern.compile(" start=\".*?\"");

    Pattern table = Pattern.compile("<table[^>]*?>.*?</table>");
    Pattern img = Pattern.compile("<img.*?>");


    String ltUnicode = Character.toString((char) 44000);
    String gtUnicode = Character.toString((char) 44001);
    Pattern ltToUnicode = Pattern.compile("&lt;");
    Pattern gtToUnicode = Pattern.compile("&gt;");
    Pattern unicodeToLt = Pattern.compile(ltUnicode);
    Pattern unicodeToGt = Pattern.compile(gtUnicode);

    public String convert(String input) {
        String body = extractBody(input);
        String css = extractCss(input);

        String out;

        out = findItalicsAndBold(body, css);

        out = removeEmptyATags(out);
        out = removeClasses(out);
        out = removeSpans(out);
        out = removeComments(out);
        out = removeInvalidContent(out);
        out = removeListAttributes(out);
        out = removeEmptyPTags(out);
        out = unescapeHtml(out);
        out = replaceTitles(out);
        out = replaceDashes(out);

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

    private String extractCss(String in) {
        Matcher m = css.matcher(in);

        if (m.find()) {
            return m.group(1);
        }
        return  in;
    }

    private ArrayList<Pattern> initTitles(){
        ArrayList<Pattern> hTags = new ArrayList<>();
        for(int i=1; i<5; i++){
            hTags.add(Pattern.compile("<h" + i + " [^>]*>"));
        }
        return hTags;
    }

    /**
     * Bold and italics are not marked with tags in GDocs, instead it is applied with CSS.
     * For instance:
     * .c1{font-weight:bold}
     * lalala <span class="c1">bold</span>
     *
     * The classnames change each time, so need to dynamicall find it and change the span to <i> or <b>
     */
    private String findItalicsAndBold(String body, String css) {
        Matcher italicsMatcher = italicStyleName.matcher(css);
        Matcher boldMatcher = boldStyleName.matcher(css);

        int start = 0;
        ArrayList<Pattern> boldClasses = new ArrayList<>();
        while(boldMatcher.find(start)) {
            boldClasses.add(Pattern.compile("<span class=\"[^\"]*" + boldMatcher.group(1) + "[^\"]*\">(.*?)</span>"));
            start = boldMatcher.start() + 1;
        }

        start = 0;
        while (italicsMatcher.find(start)) {
            Pattern italicClasses = Pattern.compile("<span class=\"[^\"]*" + italicsMatcher.group(1) + "[^\"]*\">(.*?)</span>");
            Matcher spanMatcherItalics = italicClasses.matcher(body);
            
            StringBuffer sb = new StringBuffer();
            while(spanMatcherItalics.find()) {
                boolean italicbold = false;
                for(Pattern boldClass : boldClasses) {
                    Matcher spanMatcherBold = boldClass.matcher(spanMatcherItalics.group());
                    if(spanMatcherBold.matches()) {
                        italicbold = true;
                    }
                }
                String replaceString = italicbold ? "<i><b>$1</b></i>" : "<i>$1</i>";
                spanMatcherItalics.appendReplacement(sb, replaceString);
            }
            body = spanMatcherItalics.appendTail(sb).toString();
            start = italicsMatcher.start() + 1;
        }

        for (Pattern boldClass : boldClasses) {
            Matcher spanMatcherBold = boldClass.matcher(body);
            body = spanMatcherBold.replaceAll("<b>$1</b>");
        }

        return body;
    }

    /**
     * Remove <a name=*></a> stuff google inserts everywhere
     */
    private String removeEmptyATags(String in) {
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
     * Removing ids from header tags
     */
    private String replaceTitles(String in){
        String out = in;
        for(int i = 0; i < 4; i++){
            Matcher m = hTags.get(i).matcher(out);
            out = m.replaceAll("<h" + (i + 1) + ">");
        }
        return out;
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

    /*
     * Replaces "--" with an en-dash
     */
    private String replaceDashes(String in) {
        Matcher m = dashes.matcher(in);
        return m.replaceAll("–");
    }

    /**
     * Converts HTML entities to "normal characters", for instance
     * it converts &aring; to å
     *
     * But &lt; (<) and &gt; (>) are ignored, to avoid < and > in the written
     * text to affect our HTML.
     */
    private String unescapeHtml(String in) {
        // replace all &gt; and &lt;
        Matcher m = ltToUnicode.matcher(in);
        String out = m.replaceAll(ltUnicode);

        m = gtToUnicode.matcher(out);
        out = m.replaceAll(gtUnicode);

        //Convert quotes to "guillemets"
        out = out.replaceAll("&ldquo;","«");
        out = out.replaceAll("&rdquo;","»");

        // convert stuff
        out = StringEscapeUtils.unescapeHtml4(out);

        // add the &gt; and &lt;s back
        m = unicodeToLt.matcher(out);
        out = m.replaceAll("&lt;");

        m = unicodeToGt.matcher(out);
        out = m.replaceAll("&gt;");

        return out;
    }
}
