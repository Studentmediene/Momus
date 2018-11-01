package no.dusken.momus.service.repository;

import no.dusken.momus.model.IllustrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IllustrationRequestRepository extends JpaRepository<IllustrationRequest, Long> {
    List<IllustrationRequest> findByStatusIn(List<IllustrationRequest.Status> statuses);
    List<IllustrationRequest> findByStatusInAndRequesterId(List<IllustrationRequest.Status> statuses, Long personId);
    List<IllustrationRequest> findByRequesterId(Long personId);
}
