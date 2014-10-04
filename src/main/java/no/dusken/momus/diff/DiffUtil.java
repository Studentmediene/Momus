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

    public @ResponseBody String getDiffString(long art, long oldId, long newId){
        List<ArticleRevision> revision = articleRevisionRepository.findByArticle_Id(art);
        String oldText = revision.get((int) oldId).getContent();
        String newText = revision.get((int) newId).getContent();
        oldText = oldText.replace("\n","");
        newText = newText.replace("\n","");

        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        TagToUnicodeConverter tagToUnicodeConverter = new TagToUnicodeConverter();
        oldText = tagToUnicodeConverter.removeTags(oldText);
        newText = tagToUnicodeConverter.removeTags(newText);

        LinkedList<DiffMatchPatch.Diff> diffs = diffMatchPatch.diff_main(oldText, newText, false);
        diffMatchPatch.diff_cleanupSemantic(diffs);
        String diffText = diffMatchPatch.diff_prettyHtml(diffs);

        return tagToUnicodeConverter.addTags(diffText);
    }
}
