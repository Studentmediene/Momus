package no.dusken.momus.controller;


import no.dusken.momus.model.Article;
import no.dusken.momus.model.Disposition;
import no.dusken.momus.model.Page;
import no.dusken.momus.model.Section;
import no.dusken.momus.service.ArticleService;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.DispositionRepository;
import no.dusken.momus.service.repository.PageRepository;
import no.dusken.momus.service.repository.SectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/disp")
public class DispositionController {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private DispositionRepository dispositionRepository;

    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    List<Disposition> getAllDispositions() {
        return dispositionRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    Disposition getDispositionByID(@PathVariable("id") Long id) {
        Disposition one = dispositionRepository.findOne(id);
        return one;
    }

    @RequestMapping(value = "/section", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Section> getAllSections() {
        return sectionRepository.findAll();
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Disposition saveDisposition(@RequestBody Disposition disposition, @PathVariable("id") String id) {
        logger.debug("was here");

        Set<Page> pages = disposition.getPages();
        Set<Page> savedPages = new HashSet<>();
        for (Page page : pages) {
            Page saved = pageRepository.save(page);
            for (Article article : page.getArticles()) {

                articleRepository.save(article);
//                articleService.saveMetaData(article);

            }
            savedPages.add(saved);
        }
        disposition.setPages(savedPages);

        return dispositionRepository.save(disposition);
    }

}
