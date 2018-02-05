package no.dusken.momus.service.repository;

import no.dusken.momus.model.IllustrationRequest;
import no.dusken.momus.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IllustrationRequestRepository extends JpaRepository<IllustrationRequest, Long> {
    List<IllustrationRequest> getAllByStatusIn(List<IllustrationRequest.Status> statuses);
    List<IllustrationRequest> getAllByStatusInAndRequester(List<IllustrationRequest.Status> statuses, Person person);
    List<IllustrationRequest> getAllByRequester(Person person);
}
