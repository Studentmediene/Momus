/*
 * Copyright 2014 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.dusken.momus.controller;

import no.dusken.momus.model.Disposition;
import no.dusken.momus.model.Publication;
import no.dusken.momus.service.repository.DispositionRepository;
import no.dusken.momus.service.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequestMapping("/publication")
public class PublicationController {

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private DispositionRepository dispositionRepository;

    /**
     * Returns a repository with publications within the year given by the parameter.
     *
     * @method getAllPublications
     * @param year  the year you want to see publications
     * @return      the publications in the given year
     */
    @RequestMapping(value = "/year/{year}", method = RequestMethod.GET)
    public @ResponseBody List<Publication> getAllPublications(@PathVariable("year") int year ){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, Calendar.JANUARY, 1);
        Date date = calendar.getTime();

        calendar.set(year + 1, Calendar.JANUARY, 1);
        Date date2 = calendar.getTime();

        return publicationRepository.findByReleaseDateBetween(date, date2);
    }

    /**
     * Return a repository with all the active publications from now, and ten year ahead.
     *
     * @method getActivePublications
     * @return the publications that are active.
     */
    @RequestMapping(value ="/activePublications", method = RequestMethod.GET)
    public @ResponseBody List<Publication> getActivePublications() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        //Just adding 10 years to the calendar to receive active publications from today and 10 years ahead.
        calendar.roll(Calendar.YEAR, 10);
        Date date2 = calendar.getTime();

        return publicationRepository.findByReleaseDateBetween(date, date2);
    }

    /**
     * This saves the publication given the parameter to the database
     *
     * @method              savePublication
     * @param id            the id of the publication that will be saved.
     * @param publication   the publication that will be saved.
     * @return              the saved publication.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody Publication savePublication(@PathVariable("id") Long id, @RequestBody Publication publication) {
        Publication savedPublication = publicationRepository.save(publication);
        savedPublication = publicationRepository.findOne(savedPublication.getId());
        return savedPublication;

    }

    /**
     * This adds the publication to the database.
     *
     * @method addPublication
     * @param publication       the publication that will be added.
     * @return                  the new publication.
     */
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Publication addPublication(@RequestBody Publication publication) {
        Publication newPublication = publicationRepository.save(publication);
        newPublication = publicationRepository.findOne(newPublication.getId());

        Disposition disposition = new Disposition(newPublication.getId());
        disposition.setPublication(newPublication);
        dispositionRepository.save(disposition);

        return newPublication;
    }
}
