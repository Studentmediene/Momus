package no.dusken.momus.diff;

import java.util.HashMap;
import java.util.Map;

public class TagToUnicodeConverter {

    private Map<String, Integer> tagsAndReplacement = new HashMap<>();

    public TagToUnicodeConverter() {
        setUpTags();
    }

    public String removeTags(String text) {
        for (Map.Entry<String, Integer> stringIntegerEntry : tagsAndReplacement.entrySet()) {
            text = text.replace(stringIntegerEntry.getKey(), Character.toString((char) stringIntegerEntry.getValue().intValue()));
        }
        return text;
    }

    public String addTags(String text) {
        for (Map.Entry<String, Integer> stringIntegerEntry : tagsAndReplacement.entrySet()) {
            text = text.replace(Character.toString((char) stringIntegerEntry.getValue().intValue()), stringIntegerEntry.getKey());
        }
        return text;
    }



    private void setUpTags() {
        int unicodeChar = 44032;
        tagsAndReplacement.put("<span>", unicodeChar++);
        tagsAndReplacement.put("</span>", unicodeChar++);
        tagsAndReplacement.put("<h1>", unicodeChar++);
        tagsAndReplacement.put("</h1>", unicodeChar++);
        tagsAndReplacement.put("<h2>", unicodeChar++);
        tagsAndReplacement.put("</h2>", unicodeChar++);
        tagsAndReplacement.put("<h3>", unicodeChar++);
        tagsAndReplacement.put("</h3>", unicodeChar++);
        tagsAndReplacement.put("<h4>", unicodeChar++);
        tagsAndReplacement.put("</h4>", unicodeChar++);
        tagsAndReplacement.put("<br>", unicodeChar++);
        tagsAndReplacement.put("<b>", unicodeChar++);
        tagsAndReplacement.put("</b>", unicodeChar++);
        tagsAndReplacement.put("<i>", unicodeChar++);
        tagsAndReplacement.put("</i>", unicodeChar++);
        tagsAndReplacement.put("<u>", unicodeChar++);
        tagsAndReplacement.put("</u>", unicodeChar++);
        tagsAndReplacement.put("<strike>", unicodeChar++);
        tagsAndReplacement.put("</strike>", unicodeChar++);
        tagsAndReplacement.put("<p>", unicodeChar++);
        tagsAndReplacement.put("</p>", unicodeChar++);
        tagsAndReplacement.put("<ul>", unicodeChar++);
        tagsAndReplacement.put("</ul>", unicodeChar++);
        tagsAndReplacement.put("<ol>", unicodeChar++);
        tagsAndReplacement.put("</ol>", unicodeChar++);
        tagsAndReplacement.put("<li>", unicodeChar++);
        tagsAndReplacement.put("</li>", unicodeChar++);
        tagsAndReplacement.put("<blockquote>", unicodeChar++);
        tagsAndReplacement.put("</blockquote>", unicodeChar++);
    }
}