package no.dusken.momus.section;

import no.dusken.momus.common.exceptions.RestException;
import no.dusken.momus.person.authorization.Role;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public Section getSectionById(Long id) {
        return sectionRepository.findById(id).orElseThrow(() -> new RestException("", HttpServletResponse.SC_NOT_FOUND));
    }

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public Optional<Section> getSectionForRole(Role role) {
        return getAllSections().stream().filter(s -> s.getEditorRole() == role || s.getJournalistRole() == role).findFirst();
    }

    public Section updateSectionRoles(Long id, Section section) {
        Section existing = getSectionById(id);
        existing.setEditorRole(section.getEditorRole());
        existing.setJournalistRole(section.getJournalistRole());
        return sectionRepository.saveAndFlush(existing);
    }
}
