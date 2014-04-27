package no.dusken.momus.controller;


import no.dusken.momus.model.Disposition;
import no.dusken.momus.model.Section;
import no.dusken.momus.service.repository.DispositionRepository;
import no.dusken.momus.service.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/disp")
public class DispositionController {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private DispositionRepository dispositionRepository;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Disposition> getAllDispositions() {
        return dispositionRepository.findAll();
    }

    @RequestMapping(value= "/{id}", method = RequestMethod.GET)
    public @ResponseBody Disposition getDispositionByID(@PathVariable("id") Long id) {
        return dispositionRepository.findOne(id);
    }

    @RequestMapping(value= "/section", method = RequestMethod.GET)
    public @ResponseBody List<Section> getAllSections(){
        return sectionRepository.findAll();
    }


    @RequestMapping(method = RequestMethod.PUT)
    public @ResponseBody Disposition saveDisposition(@RequestBody Disposition disposition){
        return dispositionRepository.save(disposition);
    }

}
