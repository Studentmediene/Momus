package no.dusken.momus.service;

import no.dusken.momus.authentication.UserDetailsService;
import no.dusken.momus.model.IllustrationRequest;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.IllustrationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class IllustrationRequestService {

    @Autowired
    private IllustrationRequestRepository illustrationRequestRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    public List<IllustrationRequest> getRequests(List<IllustrationRequest.Status> statuses) {
        if(statuses == null) {
            return illustrationRequestRepository.findAll();
        } else {
            return illustrationRequestRepository.getAllByStatusIn(statuses);
        }
    }

    public List<IllustrationRequest> getMyRequests(List<IllustrationRequest.Status> statuses) {
        Person user = userDetailsService.getLoggedInPerson();
        if(statuses == null) {
            return illustrationRequestRepository.getAllByRequester(user);
        } else {
            return illustrationRequestRepository.getAllByStatusInAndRequester(statuses, user);
        }
    }

    public IllustrationRequest createInternalRequest(IllustrationRequest request) {
        request.setExternal(false);
        request.setStatus(IllustrationRequest.Status.PENDING);
        request.setRequester(userDetailsService.getLoggedInPerson());
        return illustrationRequestRepository.saveAndFlush(request);
    }

    public IllustrationRequest createExternalRequest(IllustrationRequest request) {
        request.setExternal(true);
        request.setStatus(IllustrationRequest.Status.PENDING);
        return illustrationRequestRepository.saveAndFlush(request);
    }

    public void updateStatus(Long id, IllustrationRequest.Status status) {
        IllustrationRequest request = illustrationRequestRepository.findOne(id);
        request.setStatus(status);
        illustrationRequestRepository.saveAndFlush(request);
    }
}
