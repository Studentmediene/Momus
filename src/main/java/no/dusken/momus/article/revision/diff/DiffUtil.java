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

package no.dusken.momus.article.revision.diff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.dusken.momus.article.revision.ArticleRevision;

import java.util.LinkedList;
import java.util.List;

@Service
public class DiffUtil {
    @Autowired
    TagToUnicodeConverter tagToUnicodeConverter;

    public LinkedList<DiffMatchPatch.Diff> getDiffList(ArticleRevision oldRevision, ArticleRevision newRevision) {
        if (oldRevision.getId() > newRevision.getId()) {
            ArticleRevision tmp = newRevision;
            newRevision = oldRevision;
            oldRevision = tmp;
        }

        String oldText = oldRevision.getContent().replace("\n", "");
        String newText = newRevision.getContent().replace("\n", "");

        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        oldText = tagToUnicodeConverter.removeTags(oldText);
        newText = tagToUnicodeConverter.removeTags(newText);
        LinkedList<DiffMatchPatch.Diff> diffs = diffMatchPatch.diff_main(oldText, newText, false);
        diffMatchPatch.diff_cleanupSemantic(diffs);
        diffs = cleanUpDiffs(diffs);

        return addTagsToDiffs(diffs);
    }

    private LinkedList<DiffMatchPatch.Diff> addTagsToDiffs(LinkedList<DiffMatchPatch.Diff> diffs) {
        TagToUnicodeConverter tagToUnicodeConverter = new TagToUnicodeConverter();
        for (DiffMatchPatch.Diff diff : diffs) {
            diff.text = tagToUnicodeConverter.addTags(diff.text);
        }
        return diffs;
    }


    private LinkedList<DiffMatchPatch.Diff> cleanUpDiffs(LinkedList<DiffMatchPatch.Diff> diffs) {
        LinkedList<DiffMatchPatch.Diff> cleaned = new LinkedList<>();

        for (DiffMatchPatch.Diff diff : diffs) {
            cleanDiff(diff, cleaned);
        }

        return cleaned;

    }

    private void cleanDiff(DiffMatchPatch.Diff diff, List<DiffMatchPatch.Diff> cleaned) {
        if (diff.operation == DiffMatchPatch.Operation.EQUAL) {
            cleaned.add(diff);
            return;
        }

        String text = diff.text;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c >= tagToUnicodeConverter.tagStart) { // if the character is one of the unicodes we use in TagToUnicodeConverter
                DiffMatchPatch.Operation type;

                if (diff.operation == DiffMatchPatch.Operation.DELETE) {
                    type = DiffMatchPatch.Operation.DELETETAG;
                } else {
                    type = DiffMatchPatch.Operation.INSERTTAG;
                }

                String textBefore = text.substring(0, i);

                if (textBefore.length() > 0) {
                    DiffMatchPatch.Diff before = new DiffMatchPatch.Diff(diff.operation, textBefore);
                    cleaned.add(before); // this contained no unicodes
                }

                DiffMatchPatch.Diff tag = new DiffMatchPatch.Diff(type, String.valueOf(c));
                cleaned.add(tag);

                String textAfter = text.substring(i + 1);

                if (textAfter.length() > 0) {
                    DiffMatchPatch.Diff after = new DiffMatchPatch.Diff(diff.operation, textAfter);
                    cleanDiff(after, cleaned); // call recursively on the new, unchecked text
                }

                return;
            }
        }

        cleaned.add(diff); // no unicodes, so we add it
    }
}
