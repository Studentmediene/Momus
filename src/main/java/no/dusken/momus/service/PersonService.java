package no.dusken.momus.service;

import no.dusken.momus.model.Person;
import no.dusken.momus.model.Section;
import no.dusken.momus.service.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public Person updateFavouritesection(Person person, Section section) {
        person.setFavouritesection(section);

        return personRepository.saveAndFlush(person);
    }
}
