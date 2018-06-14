package no.dusken.momus.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.model.Article;
import no.dusken.momus.model.Person;
import no.dusken.momus.model.Section;
import no.dusken.momus.service.repository.ArticleRepository;
import no.dusken.momus.service.repository.PersonRepository;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PersonService {

    private final PersonRepository personRepository;
    private final ArticleRepository articleRepository;
    private final SimpUserRegistry userRegistry;

    private final Map<String, SessionState> sessionStates;

    public PersonService(
            PersonRepository personRepository,
            ArticleRepository articleRepository,
            SimpUserRegistry userRegistry
    ) {
        this.personRepository = personRepository;
        this.articleRepository = articleRepository;
        this.userRegistry = userRegistry;

        this.sessionStates = new HashMap<>();
    }

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

    public Set<Person> getAllLoggedInPersons() {
        return userRegistry.findSubscriptions(sub -> true)
                .stream()
                .map(s -> sessionStates.get(s.getSession().getId()).getPerson())
                .collect(Collectors.toSet());
    }

    public Set<Person> getLoggedInPersonsAtState(String state) {
        return userRegistry.findSubscriptions(sub -> true)
                .stream()
                .filter(s -> sessionStates.get(s.getSession().getId()).getState().equals(state))
                .map(s -> sessionStates.get(s.getSession().getId()).getPerson())
                .collect(Collectors.toSet());
    }

    public void startSessionForPerson(String sessid, String username) {
        Person person = personRepository.findByUsername(username);
        log.debug("Starting session {} for user {}", sessid, person);
        sessionStates.put(sessid, new SessionState(person, ""));
    }

    public void setStateForSession(String sessid, String state) {
        if(sessionStates.containsKey(sessid)) {
            sessionStates.get(sessid).setState(state);
        }
    }

    public void endSession(String sessid) {
        log.debug("Ending session {} which was for user {}", sessid, sessionStates.get(sessid).getPerson());
        sessionStates.remove(sessid);
    }

    @AllArgsConstructor
    private class SessionState {
        @Getter
        private Person person;
        @Getter @Setter
        private String state;
    }
}
