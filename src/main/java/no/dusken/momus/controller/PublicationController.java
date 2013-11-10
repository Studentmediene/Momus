package no.dusken.momus.controller;

import no.dusken.momus.model.Publication;
import no.dusken.momus.service.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Service
@RequestMapping("/publication")
public class PublicationController {

    @Autowired
    private PublicationRepository publicationRepository;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Publication> getAllPublications() {
        return publicationRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody Publication savePublication(@PathVariable("id") Long id, @RequestBody Publication publication) {
        publicationRepository.save(publication);
        return publication;
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Publication addPublication(@RequestBody Publication publication) {
        publicationRepository.save(publication);
        return publication;
    }
}
