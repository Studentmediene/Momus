package no.dusken.momus.service.repository;

import no.dusken.momus.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SourceRepository extends JpaRepository<Source, Long> {

    @Query("select s from Source s join s.tags t where t.tag = (:tag)")
    public List<Source> findByTag(@Param("tag") String tag);

}
