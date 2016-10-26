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

import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.ArticleRevision;
import no.dusken.momus.service.repository.ArticleRevisionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;

@Service
public class DiffUtil {

    @Autowired
    private ArticleRevisionRepository articleRevisionRepository;

    @Autowired
    TagToUnicodeConverter tagToUnicodeConverter;

    private Logger logger = LoggerFactory.getLogger(getClass());


    public @ResponseBody LinkedList<DiffMatchPatch.Diff> getDiffList(long art, long oldId, long newId) {
        List<ArticleRevision> revision = articleRevisionRepository.findByArticleIdOrderBySavedDateDesc(art);

        if (oldId > newId) {
            long tmp = newId;
            newId = oldId;
            oldId = tmp;
        }

        String oldText = getRevisionContentById(revision, oldId).replace("\n", "");
        String newText = getRevisionContentById(revision, newId).replace("\n", "");

        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        oldText = tagToUnicodeConverter.removeTags(oldText);
        newText = tagToUnicodeConverter.removeTags(newText);
        LinkedList<DiffMatchPatch.Diff> diffs = diffMatchPatch.diff_main(oldText, newText, false);
        diffMatchPatch.diff_cleanupSemantic(diffs);
        diffs = cleanUpDiffs(diffs);

        return addTagsToDiffs(diffs);

    }

    public LinkedList<DiffMatchPatch.Diff> addTagsToDiffs(LinkedList<DiffMatchPatch.Diff> diffs) {
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
    private String getRevisionContentById(List<ArticleRevision> revisions, long id){
        for (ArticleRevision rev: revisions){
            if(rev.getId() == id){
                return rev.getContent();
            }
        }

        logger.info("No revision found with id {}", id);
        throw new RestException("No revision with that ID", 400);
    }

}
