package no.dusken.momus.service.remotedocument.sharepoint;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

public class SharepointTextConverter {
    private final String titleStyleName = "Tittel";
    private final String preambleStyleName = "Ingress";
    private final String subtitleStyleName = "Mellomtittel";

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

    Pattern title = Pattern.compile("<p [^>]*class=\"[^\"]*" + titleStyleName + "[^>]*>([^<]*)</p>");
    Pattern preamble = Pattern.compile("<p [^>]*class=\"[^\"]*" + preambleStyleName + "[^>]*>([^<]*)</p>");
    Pattern subtitle = Pattern.compile("<p [^>]*class=\"[^\"]*" + subtitleStyleName + "[^>]*>([^<]*)</p>");
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

    private List<Pattern> initTitles(){
        List<Pattern> hTags = new ArrayList<>();
        for(int i=1; i<5; i++){
            hTags.add(Pattern.compile("<h" + i + " [^>]*>"));
        }
        return hTags;
    }

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
     * Removes some stuff from the lists
     */
    private String removeListAttributes(String in) {
        Matcher m = lists.matcher(in);
        return m.replaceAll("");
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