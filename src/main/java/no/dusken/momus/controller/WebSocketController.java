package no.dusken.momus.controller;

import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.PersonRepository;
import org.springframework.context.event.EventListener;
import org.springframework.data.util.Pair;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class WebSocketController {

    private final SimpUserRegistry userRegistry;

    private final Map<String, Pair<Person, String>> sessionStates;

    private final PersonRepository personRepository;

    public WebSocketController(SimpUserRegistry userRegistry, PersonRepository personRepository) {
        this.personRepository = personRepository;
        this.userRegistry = userRegistry;
        sessionStates = new HashMap<>();
    }

    @GetMapping("/users")
    public @ResponseBody Set<Person> getLoggedInUsers(@RequestParam String state) {
        return userRegistry.findSubscriptions(sub -> true)
                .stream()
                .filter(s -> sessionStates.get(s.getSession().getId()).getSecond().equals(state))
                .map(s -> sessionStates.get(s.getSession().getId()).getFirst())
                .collect(Collectors.toSet());
    }

    @PutMapping("/users/{sessid}")
    public void setMyState(@PathVariable("sessid") String sessid, @RequestParam String state, Principal principal) {
        if(sessionStates.containsKey(sessid)) {
            sessionStates.put(sessid, Pair.of(sessionStates.get(sessid).getFirst(), state));
        }else {
            sessionStates.put(sessid, Pair.of(personRepository.findByUsername(principal.getName()), state));
        }
    }

    @EventListener
    public void handleSessionEnd(SessionDisconnectEvent disconnectEvent) {
        sessionStates.remove(disconnectEvent.getSessionId());
    }
}