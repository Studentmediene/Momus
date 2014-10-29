package no.dusken.momus.service.repository;

import no.dusken.momus.model.SavedSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedSearchRepository extends JpaRepository<SavedSearch, Long>{

    public List<SavedSearch> findByOwner_Id(Long owner);
}
