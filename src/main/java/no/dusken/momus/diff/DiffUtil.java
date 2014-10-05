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
        diffs = cleanUpDiffs(diffs);
        String diffText = diffMatchPatch.diff_prettyHtml(diffs);

        return tagToUnicodeConverter.addTags(diffText);
    }

    public LinkedList<DiffMatchPatch.Diff> cleanUpDiffs(LinkedList<DiffMatchPatch.Diff> diffs){
        for (int i = 0; i < diffs.size();i++) {
            if(diffs.get(i).operation == DiffMatchPatch.Operation.DELETE || diffs.get(i).operation == DiffMatchPatch.Operation.INSERT){
                for ( int j = 0; j < diffs.get(i).text.length();j++){
                    if((int) diffs.get(i).text.toCharArray()[j] >=44035){
                        int endOld = j;
                        DiffMatchPatch.Diff tags = new DiffMatchPatch.Diff(DiffMatchPatch.Operation.EQUAL, "");
                        for( int a = j; a<diffs.get(i).text.length();a++){
                            if((int) diffs.get(i).text.toCharArray()[a] >=44035){
                                tags.text += diffs.get(i).text.toCharArray()[j];
                                j++;
                            }else{
                                break;
                            }
                        }
                        DiffMatchPatch.Diff etter = new DiffMatchPatch.Diff(diffs.get(i).operation, diffs.get(i).text.substring(j));
                        diffs.get(i).text = diffs.get(i).text.substring(0, endOld);
                        diffs.add(i+1, tags);
                        diffs.add(i+2, etter);
                        i+=2;

                    }
                }
            }
        }
        return diffs;
    }
}
