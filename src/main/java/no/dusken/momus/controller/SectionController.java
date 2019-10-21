package no.dusken.momus.controller;

import no.dusken.momus.model.Section;
import no.dusken.momus.service.SectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/section")
public class SectionController {

    private final SectionService sectionService;

    SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping("")
    public List<Section> getAllSections() {
        return sectionService.getAllSections();
    }

    @PatchMapping("/{id}/roles")
    public Section updateSectionRoles(@PathVariable Long id, @RequestBody Section section) {
        return sectionService.updateSectionRoles(id, section);
    }
}
