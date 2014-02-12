package no.dusken.momus.controller;

import no.dusken.momus.model.Source;
import no.dusken.momus.model.SourceTag;
import no.dusken.momus.service.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/source")
public class SourceController {

    @Autowired
    private SourceService sourceService;


    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Source> getAllSources() {
        return sourceService.getSourceRepository().findAll();
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    public @ResponseBody Source createSource(@RequestBody Source newSource) {
        return sourceService.save(newSource);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody Source getSourceById(@PathVariable("id") Long id) {
        return sourceService.getSourceRepository().findOne(id);
    }
    




    @RequestMapping(value = "/tag", method = RequestMethod.GET)
    public @ResponseBody List<SourceTag> getAllTags() {
        return sourceService.getTagRepository().findAll();
    }

    @RequestMapping(value = "/tag", method = RequestMethod.POST)
    public @ResponseBody SourceTag createTag(@RequestBody SourceTag newTag) {
        return sourceService.getTagRepository().save(newTag);
    }

    @RequestMapping(value = "/tag/{tagId}", method = RequestMethod.PUT)
    public @ResponseBody SourceTag updateTag(@RequestBody SourceTag newTag, @PathVariable("tagId") String tagId) {
        return sourceService.updateTag(new SourceTag(tagId), newTag);
    }

    @RequestMapping(value = "/tag/{tagId}", method = RequestMethod.DELETE)
    public @ResponseBody SourceTag deleteTag(@RequestBody SourceTag newTag, @PathVariable("tagId") String tagId) {
        return sourceService.updateTag(new SourceTag(tagId), newTag);
    }

    @RequestMapping(value = "/tag/unused", method = RequestMethod.GET)
    public @ResponseBody List<SourceTag> getUnusedTags() {
        return sourceService.getTagRepository().getUnusedTags();
    }



}
