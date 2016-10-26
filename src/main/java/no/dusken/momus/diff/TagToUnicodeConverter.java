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

package no.dusken.momus.diff;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts parts of strings to a single character, so
 * they will not be split up by a diff algorithm.
 *
 * Example:   <h3> changed to <h4> could give the diff <h/3/4> which would
 * be an invalid tag. So instead we make that two symbols instead that are diffed whole.
 */
@Service
public class TagToUnicodeConverter {

    private Map<String, Integer> tagsAndReplacement = new HashMap<>();

    public TagToUnicodeConverter() {
        setUpTags();
    }

    public final int specialStart = 44000;
    public final int tagStart = 45000;

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
        // Things that shouldn't be split but aren't tags
        int unicodeChar = specialStart;
        tagsAndReplacement.put("&gt;", unicodeChar++);
        tagsAndReplacement.put("&lt", unicodeChar++);

        unicodeChar = tagStart;
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