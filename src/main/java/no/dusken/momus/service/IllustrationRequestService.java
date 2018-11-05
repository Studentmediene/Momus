package no.dusken.momus.service;

import no.dusken.momus.authentication.UserDetailsService;
import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.IllustrationRequest;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.IllustrationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
            return illustrationRequestRepository.findByStatusIn(statuses);
        }
    }

    public IllustrationRequest getRequestById(Long id) {
        if(!illustrationRequestRepository.exists(id)) {
            throw new RestException("No illustration request with id=" + id + " was found", 404);
        }

        return illustrationRequestRepository.findOne(id);
    }

    public List<IllustrationRequest> getMyRequests(List<IllustrationRequest.Status> statuses) {
        Person user = userDetailsService.getLoggedInPerson();
        if(statuses == null) {
            return illustrationRequestRepository.findByRequesterId(user.getId());
        } else {
            return illustrationRequestRepository.findByStatusInAndRequesterId(statuses, user.getId());
        }
    }

    public IllustrationRequest createInternalRequest(IllustrationRequest request) {
        request.setId(request.getArticle().getId());
        request.setExternal(false);
        request.setCreated(LocalDateTime.now());
        request.setStatus(IllustrationRequest.Status.PENDING);
        request.setRequester(userDetailsService.getLoggedInPerson());
        return illustrationRequestRepository.saveAndFlush(request);
    }

    public IllustrationRequest createExternalRequest(IllustrationRequest request) {
        request.setExternal(true);
        request.setCreated(LocalDateTime.now());
        request.setStatus(IllustrationRequest.Status.PENDING);
        return illustrationRequestRepository.saveAndFlush(request);
    }

    public IllustrationRequest updateRequest(Long id, IllustrationRequest request) {
        IllustrationRequest existing = getRequestById(id);

        existing.setDescription(request.getDescription());
        existing.setNumberOfIllustrations(request.getNumberOfIllustrations());
        existing.setNumberOfPages(request.getNumberOfPages());

        if (existing.getStatus().equals(IllustrationRequest.Status.DENIED)) {
            existing.setStatus(IllustrationRequest.Status.PENDING);
        }

        return illustrationRequestRepository.saveAndFlush(existing);
    }

    public void updateStatus(Long id, IllustrationRequest.Status status) {
        IllustrationRequest request = getRequestById(id);
        request.setStatus(status);
        illustrationRequestRepository.saveAndFlush(request);
    }

    public void updateIllustratorComment(Long id, String illustratorComment) {
        IllustrationRequest request = getRequestById(id);
        request.setIllustratorComment(illustratorComment);
        illustrationRequestRepository.saveAndFlush(request);
    }
}
