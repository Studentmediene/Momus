package no.dusken.momus.diff;

import no.dusken.momus.model.ArticleRevision;
import no.dusken.momus.service.repository.ArticleRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;

@Service
public class DiffUtil {

    @Autowired
    private ArticleRevisionRepository articleRevisionRepository;

    public @ResponseBody LinkedList<DiffMatchPatch.Diff> getDiffList(long art, long oldId, long newId) {
        List<ArticleRevision> revision = articleRevisionRepository.findByArticleIdOrderBySavedDateDesc(art);

        if (oldId > newId) {
            long tmp = newId;
            newId = oldId;
            oldId = tmp;
        }

        String oldText = revision.get((int) oldId).getContent().replace("\n", "");
        String newText = revision.get((int) newId).getContent().replace("\n", "");

        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        TagToUnicodeConverter tagToUnicodeConverter = new TagToUnicodeConverter();
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

            if (c >= 44032) { // if the character is one of the unicodes we use in TagToUnicodeConverter
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
