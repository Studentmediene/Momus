package no.dusken.momus.service.repository;

import no.dusken.momus.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
