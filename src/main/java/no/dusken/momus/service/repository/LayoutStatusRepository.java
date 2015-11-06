package no.dusken.momus.service.repository;

import no.dusken.momus.model.LayoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LayoutStatusRepository extends JpaRepository<LayoutStatus, Long>{
    public LayoutStatus findByName(String name);
}
