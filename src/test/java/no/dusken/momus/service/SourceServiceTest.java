package no.dusken.momus.service;

import no.dusken.momus.model.Source;
import no.dusken.momus.model.SourceTag;
import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.*;

@Transactional
public class SourceServiceTest extends AbstractTestRunner {

    @Autowired
    SourceService sourceService;

    @Test
    public void testNewTagsAreCreated() {
        Set<SourceTag> tags = new HashSet<>();
        tags.add(new SourceTag("tag-1"));
        tags.add(new SourceTag("tag-2"));

        Source source = new Source("Kilde Kildesen", tags);
        sourceService.save(source);

        List<SourceTag> allTags = sourceService.getTagRepository().findAll();

        assertEquals(2, allTags.size());
    }

    @Test
    public void testWeDontGetDuplicateTags() {
        Set<SourceTag> tags = new HashSet<>();
        tags.add(new SourceTag("tag-1"));
        tags.add(new SourceTag("tag-2"));

        Source source = new Source("Kilde Kildesen", tags);
        sourceService.save(source);


        Set<SourceTag> tags2 = new HashSet<>();
        tags2.add(new SourceTag("tag-1"));
        tags2.add(new SourceTag("tag-3"));

        Source source2 = new Source("Løgn Løgnersen", tags2);
        sourceService.save(source2);

        List<SourceTag> allTags = sourceService.getTagRepository().findAll();
        assertEquals(3, allTags.size());
    }

    @Test
    public void testTagRemainsAfterBeingRemovedFromSource() {
        Set<SourceTag> tags = new HashSet<>();
        tags.add(new SourceTag("tag-1"));
        tags.add(new SourceTag("tag-2"));

        Source source = new Source("Kilde Kildesen", tags);
        source = sourceService.save(source);

        tags = new HashSet<>();
        tags.add(new SourceTag("tag-1"));
        source.setTags(tags);
        source = sourceService.save(source);

        List<SourceTag> allTags = sourceService.getTagRepository().findAll();

        assertEquals(2, allTags.size());
        assertEquals(1, source.getTags().size());
    }

    @Test
    public void testDeletingTagAlsoDeletesItFromSources() {
        Set<SourceTag> tags = new HashSet<>();
        tags.add(new SourceTag("tag-1"));
        tags.add(new SourceTag("tag-2"));
        Source source = new Source("Kilde Kildesen", tags);

        Set<SourceTag> tags2 = new HashSet<>();
        tags2.add(new SourceTag("tag-1"));
        tags2.add(new SourceTag("tag-3"));
        Source source2 = new Source("Løgn Løgnersen", tags2);

        source = sourceService.save(source);
        source2 = sourceService.save(source2);

        sourceService.deleteTag(new SourceTag("tag-1"));

        List<SourceTag> allTags = sourceService.getTagRepository().findAll();
        assertEquals(2, allTags.size());
        assertEquals(1, source.getTags().size());
        assertEquals(1, source2.getTags().size());
    }

    @Test
    public void testRenamingTagUpdatesSources() {
        Set<SourceTag> tags = new HashSet<>();
        tags.add(new SourceTag("tag-1"));
        tags.add(new SourceTag("tag-2"));
        Source source = new Source("Kilde Kildesen", tags);

        source = sourceService.save(source);



        SourceTag tag = sourceService.getTagRepository().findOne("tag-2");
        SourceTag newTag = new SourceTag("new-tag");

        sourceService.updateTag(tag, newTag);

        List<SourceTag> allTags = sourceService.getTagRepository().findAll();
        source = sourceService.getSourceRepository().findOne(source.getId());

        Set<SourceTag> updatedTags = new HashSet<>();
        updatedTags.add(new SourceTag("tag-1"));
        updatedTags.add(new SourceTag("new-tag"));


        assertEquals(2, allTags.size());



        assertEquals(updatedTags, source.getTags());
    }

}
