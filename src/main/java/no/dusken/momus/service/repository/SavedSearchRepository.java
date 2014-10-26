package no.dusken.momus.service.repository;

import no.dusken.momus.model.SavedSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedSearchRepository extends JpaRepository<SavedSearch, Long>{

    public SavedSearch getByOwnerId(Long owner);
}
