package no.dusken.momus.service.repository;

import no.dusken.momus.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;


public interface PageRepository extends JpaRepository<Page, Long> {

    public List<Page> findByPublicationId(Long id);

    public List<Page> findByPublicationIdOrderByPageNrAsc(Long id);

    public void deleteByPublicationId(Long id);

}
