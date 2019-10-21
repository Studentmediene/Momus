package no.dusken.momus.service;

import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.authorization.Role;
import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.Section;
import no.dusken.momus.service.repository.SectionRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(
            SectionRepository sectionRepository
    ) {
        this.sectionRepository = sectionRepository;
    }

    public List<Section> getAllSections(){
        return sectionRepository.findAll();
    }

    public List<Role> getRoles() {
        return Arrays.stream(Role.values()).collect(Collectors.toList());
    }

    public Section updateSectionRoles(Long id, Section section) {
        Section existing = sectionRepository.findById(id).orElseThrow(() -> new RestException("", HttpServletResponse.SC_NOT_FOUND));
        existing.setEditorRole(section.getEditorRole());
        existing.setJournalistRole(section.getJournalistRole());
        return sectionRepository.saveAndFlush(existing);
    }
}
