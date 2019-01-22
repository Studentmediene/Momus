package no.dusken.momus.controller;

import no.dusken.momus.authorization.IllustratorAuthorization;
import no.dusken.momus.model.IllustrationRequest;
import no.dusken.momus.service.IllustrationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/illustrationrequests")
public class IllustrationRequestController {

    @Autowired
    private IllustrationRequestService illustrationRequestService;

    @PostMapping
    public @ResponseBody IllustrationRequest createInternalRequest(@RequestBody IllustrationRequest request) {
        return illustrationRequestService.createInternalRequest(request);
    }

    @PostMapping("/external")
    public @ResponseBody IllustrationRequest createExternalRequest(@RequestBody IllustrationRequest request) {
        return illustrationRequestService.createExternalRequest(request);
    }

    @GetMapping("/{id}")
    public @ResponseBody IllustrationRequest getRequestById(@PathVariable Long id) {
        return illustrationRequestService.getRequestById(id);
    }

    @GetMapping
    @IllustratorAuthorization
    public @ResponseBody List<IllustrationRequest> getRequests(
            @RequestParam(required = false) List<IllustrationRequest.Status> statuses) {
        return illustrationRequestService.getRequests(statuses);
    }

    @GetMapping("/mine")
    public @ResponseBody List<IllustrationRequest> getMyRequests(
            @RequestParam(required = false) List<IllustrationRequest.Status> statuses) {
        return illustrationRequestService.getMyRequests(statuses);
    }

    @PatchMapping("/{id}/request")
    public IllustrationRequest updateRequest(@PathVariable Long id, @RequestBody IllustrationRequest request) {
        return illustrationRequestService.updateRequest(id, request);
    }

    @PatchMapping("/{id}/status")
    @IllustratorAuthorization
    public void updateStatus(@PathVariable Long id, @RequestParam IllustrationRequest.Status status) {
        illustrationRequestService.updateStatus(id, status);
    }

    @PatchMapping("/{id}/illustratorcomment")
    @IllustratorAuthorization
    public void updateIllustratorComment(@PathVariable Long id, @RequestBody String illustratorComment) {
        illustrationRequestService.updateIllustratorComment(id, illustratorComment);
    }
}
