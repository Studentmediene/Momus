package no.dusken.momus.service;

import no.dusken.momus.model.Article;
import no.dusken.momus.model.Person;
import no.dusken.momus.model.Section;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ArticleRepository articleRepository;

    public Person updateFavouritesection(Person person, Section section) {
        person.setFavouritesection(section);

        return personRepository.saveAndFlush(person);
    }

    public Set<Person> getActivePersonsAndArticleContributors(List<Long> articleIds) {
        Set<Person> persons = personRepository.findByActiveTrue();

        for(Long articleId : articleIds) {
            if(articleRepository.exists(articleId)){
                Article article = articleRepository.findOne(articleId);
                persons.addAll(article.getJournalists());
                persons.addAll(article.getPhotographers());
            }
        }

        return persons;
    }
}
