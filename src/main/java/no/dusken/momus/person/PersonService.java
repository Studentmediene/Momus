package no.dusken.momus.person;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.article.ArticleService;
import no.dusken.momus.common.exceptions.RestException;

import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
public class PersonService {

    private final PersonRepository personRepository;
    private final ArticleService articleService;
    private final AvatarRepository avatarRepository;
    private final SimpUserRegistry userRegistry;

    private final Map<String, SessionState> sessionStates;

    public PersonService(
            PersonRepository personRepository,
            ArticleService articleService,
            AvatarRepository avatarRepository,
            SimpUserRegistry userRegistry
    ) {
        this.personRepository = personRepository;
        this.articleService = articleService;
        this.avatarRepository = avatarRepository;
        this.userRegistry = userRegistry;

        this.sessionStates = new HashMap<>();
    }

    public Person getPersonById(Long id) {
        return personRepository.findById(id).orElseThrow(() -> new RestException("Not found", HttpServletResponse.SC_NOT_FOUND));
    }

    public Set<Person> getActivePeopleAndArticleContributors(List<Long> articleIds) {
        Set<Person> persons = personRepository.findByActiveTrue();

        articleService.getArticlesByIds(articleIds).forEach(article -> {
            persons.addAll(article.getJournalists());
            persons.addAll(article.getPhotographers());
        });

        return persons;
    }

    public Avatar getPersonPhoto(Long id) {
        return avatarRepository.findById(id)
            .orElseThrow(() -> new RestException("No photo found for user", HttpServletResponse.SC_NOT_FOUND));
    }

    public Set<Person> getAllLoggedInPeople() {
        return userRegistry.findSubscriptions(sub -> true)
                .stream()
                .map(s -> sessionStates.get(s.getSession().getId()).getPerson())
                .collect(Collectors.toSet());
    }

    public Set<Person> getLoggedInPeopleAtState(String state) {
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
