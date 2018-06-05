package no.dusken.momus.controller;

import no.dusken.momus.model.Person;
import no.dusken.momus.model.websocket.UserEventMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class WebSocketController {

    private final SimpUserRegistry userRegistry;

    public WebSocketController(SimpUserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    @MessageMapping("/users")
    @SendTo("/ws/user")
    public UserEventMessage sendUserEvent(UserEventMessage message, Principal user) {
        return message;
    }

    @GetMapping("/users")
    public @ResponseBody Set<String> getLoggedInUsers() {
        return userRegistry.getUsers().stream().map(SimpUser::getName).collect(Collectors.toSet());
    }
}