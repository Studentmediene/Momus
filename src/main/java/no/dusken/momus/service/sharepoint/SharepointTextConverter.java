package no.dusken.momus.service.sharepoint;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

public class SharepointTextConverter {
    Pattern body = Pattern.compile("<body.*?><div.*?>(.*)</div></body>");
    Pattern css = Pattern.compile("<style type=\"text/css\">(.*)</style>");
    Pattern boldAndItalicsStyle = Pattern.compile("<span[^>]*style=\"[^\"]*font-weight:bold;font-style:italic[^>]*>([^<]*)</span>");
    Pattern italicStyle = Pattern.compile("<span[^>]*style=\"[^\"]*font-style:italic[^>]*>([^<]*)</span>");
    Pattern boldStyle = Pattern.compile("<span[^>]*style=\"[^\"]*font-weight:bold[^>]*>([^<]*)</span>");

    Pattern aTags = Pattern.compile("<a[^>]*?></a>");
    Pattern classes = Pattern.compile(" class=\".*?\"");
    Pattern spans = Pattern.compile("</?span.*?>");
    Pattern empty = Pattern.compile("<[pbi]>\\s?</[pbi]>");

    List<Pattern> hTags = initTitles();

    Pattern dashes = Pattern.compile("--");
    Pattern inlineComments = Pattern.compile("<sup>.*?</sup>");
    Pattern spaces = Pattern.compile("&nbsp;");
    Pattern comments = Pattern.compile("<div><p>.*?</p></div>");

    Pattern title = Pattern.compile("<p [^>]*class=\"[^\"]*Heading1[^>]*>([^<]*)</p>");
    Pattern preamble = Pattern.compile("<p [^>]*class=\"[^\"]*Heading2[^>]*>([^<]*)</p>");
    Pattern subtitle = Pattern.compile("<p [^>]*class=\"[^\"]*Heading3[^>]*>([^<]*)</p>");
    Pattern lists = Pattern.compile("<p [^>]*class=\"[^\"]*ListParagraph[^>]*>([^<]*)</p>");

    Pattern table = Pattern.compile("<table[^>]*?>.*?</table>");
    Pattern img = Pattern.compile("<img.*?>");


    String ltUnicode = Character.toString((char) 44000);
    String gtUnicode = Character.toString((char) 44001);
    Pattern ltToUnicode = Pattern.compile("&lt;");
    Pattern gtToUnicode = Pattern.compile("&gt;");
    Pattern unicodeToLt = Pattern.compile(ltUnicode);
    Pattern unicodeToGt = Pattern.compile(gtUnicode);

    public String convert(String input) {
        String out = extractBody(input);

        out = findItalicsAndBold(out);
        out = unescapeHtml(out);
        out = removeSpans(out);
        out = replaceTitles(out);
        out = removeEmptyTags(out);
        out = removeListAttributes(out);
        out = removeClasses(out);

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

    private List<Pattern> initTitles(){
        List<Pattern> hTags = new ArrayList<>();
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
    private String findItalicsAndBold(String body) {
        Matcher spanMatcherBoldAndItalics = boldAndItalicsStyle.matcher(body);
        body = spanMatcherBoldAndItalics.replaceAll("<b><i>$1</i></b>");

        Matcher spanMatcherBold = boldStyle.matcher(body);
        body = spanMatcherBold.replaceAll("<b>$1</b>");

        Matcher spanMatcherItalics = italicStyle.matcher(body);
        body = spanMatcherItalics.replaceAll("<i>$1</i>");

        Matcher uselessBold = Pattern.compile("</b><b>").matcher(body);
        body = uselessBold.replaceAll("");

        Matcher uselessItalic = Pattern.compile("</i><i>").matcher(body);
        body = uselessItalic.replaceAll("");

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
        Matcher h1 = title.matcher(in);
        String out = h1.replaceAll("<h1>$1</h1>");

        Matcher h2 = preamble.matcher(out);
        out = h2.replaceAll("<h2>$1</h2>");

        Matcher h3 = subtitle.matcher(out);
        out = h3.replaceAll("<h3>$1</h3>");

        return out;

    }

    /**
     * In case someone likes to have much space between their paragraphs..
     */
    private String removeEmptyTags(String in) {
        Matcher m = empty.matcher(in);
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