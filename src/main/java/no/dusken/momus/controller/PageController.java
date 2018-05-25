package no.dusken.momus.controller;

import com.fasterxml.jackson.annotation.JsonView;
import no.dusken.momus.mapper.SerializationViews;
import no.dusken.momus.model.LayoutStatus;
import no.dusken.momus.model.Page;
import no.dusken.momus.dto.PageContent;
import no.dusken.momus.service.PageService;
import no.dusken.momus.service.repository.LayoutStatusRepository;
import no.dusken.momus.service.repository.PageRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pages")
public class PageController {

    private final PageRepository pageRepository;
    private final LayoutStatusRepository layoutStatusRepository;
    private final PageService pageService;

    public PageController(PageRepository pageRepository, PageService pageService, LayoutStatusRepository layoutStatusRepository) {
        this.pageRepository = pageRepository;
        this.pageService = pageService;
        this.layoutStatusRepository = layoutStatusRepository;
    }

    @GetMapping("/{id}")
    public @ResponseBody Page getById(@PathVariable Long id) {
        return pageRepository.findOne(id);
    }

    @GetMapping
    @JsonView(SerializationViews.Simple.class)
    public @ResponseBody List<Page> getByPublicationId(@RequestParam Long publicationId) {
        return pageRepository.findByPublicationId(publicationId);
    }

    @GetMapping("/page-order")
    public @ResponseBody List<Long> getPageOrderByPublicationId(@RequestParam Long publicationId) {
        return pageRepository.getPageOrderByPublicationId(publicationId);
    }

    @PutMapping("/page-order")
    public @ResponseBody void setPageOrder(@RequestBody List<Long> pageOrder) {
        pageService.setPageOrder(pageOrder);
    }

    @PostMapping("/empty")
    public @ResponseBody List<Page> createEmptyPagesInPublication(
            @RequestParam Long publicationId,
            @RequestParam Integer afterPage,
            @RequestParam(required = false, defaultValue = "1") Integer numPages
    ) {
        return pageService.createEmptyPagesInPublication(publicationId, afterPage, numPages);
    }

    @PutMapping("/{id}/content")
    public void setContent(@PathVariable Long id, @RequestBody PageContent content) {
        pageService.setContent(id, content);
    }

    @PatchMapping("/{id}/metadata")
    public @ResponseBody Page updateMetadata(@PathVariable Long id, @RequestBody Page page) {
        return pageService.updateMetadata(id, page);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        pageRepository.delete(id);
    }

    @GetMapping("/layoutstatuscounts")
    public @ResponseBody
    Map<Long,Integer> getStatusCountsByPubId(@RequestParam Long pubid){
        List<LayoutStatus> statuses = layoutStatusRepository.findAll();
        Map<Long, Integer> map = new HashMap<>();
        for (LayoutStatus status : statuses) {
            map.put(status.getId(), this.getStatusCount(status.getName(), pubid));
        }
        return map;
    }

    @GetMapping("/layoutstatuscounts/{status}")
    public @ResponseBody int getStatusCount(@PathVariable String status, @PathVariable Long id){
        Long statusId = layoutStatusRepository.findByName(status).getId();
        return pageRepository.countByLayoutStatusIdAndPublicationId(statusId, id);
    }
}
