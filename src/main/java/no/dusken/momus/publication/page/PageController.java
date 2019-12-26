package no.dusken.momus.publication.page;

import org.springframework.web.bind.annotation.*;

import no.dusken.momus.publication.LayoutStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pages")
public class PageController {

    private final PageRepository pageRepository;
    private final PageService pageService;

    public PageController(PageRepository pageRepository, PageService pageService) {
        this.pageRepository = pageRepository;
        this.pageService = pageService;
    }

    @GetMapping("/{id}")
    public Page getById(@PathVariable Long id) {
        return pageService.getPageById(id);
    }

    @GetMapping
    public List<Page> getByPublicationId(@RequestParam Long publicationId) {
        return pageService.getPagesInPublication(publicationId);
    }

    @GetMapping("/page-order")
    public PageOrder getPageOrderByPublicationId(@RequestParam Long publicationId) {
        return pageService.getPageOrderInPublication(publicationId);
    }

    @PutMapping("/page-order")
    public void setPageOrder(@RequestBody PageOrder pageOrder) {
        pageService.setPageOrder(pageOrder);
    }

    @PostMapping("/empty")
    public List<Page> createEmptyPagesInPublication(
            @RequestParam Long publicationId,
            @RequestParam Integer afterPage,
            @RequestParam(required = false, defaultValue = "1") Integer numNewPages
    ) {
        return pageService.createEmptyPagesInPublication(publicationId, afterPage, numNewPages);
    }

    @PutMapping("/{id}/content")
    public void setContent(@PathVariable Long id, @RequestBody PageContent content) {
        pageService.setContent(id, content);
    }

    @PatchMapping("/{id}/metadata")
    public Page updateMetadata(@PathVariable Long id, @RequestBody Page page) {
        return pageService.updateMetadata(id, page);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        pageService.delete(id);
    }

    @GetMapping("/layoutstatuscounts")
    public Map<LayoutStatus, Integer> getStatusCountsByPubId(@RequestParam Long pubid){
        return Arrays.stream(LayoutStatus.values())
                .collect(Collectors.toMap(
                    t -> t,
                    t -> pageRepository.countByLayoutStatusAndPublicationId(t, pubid)));
    }
}
