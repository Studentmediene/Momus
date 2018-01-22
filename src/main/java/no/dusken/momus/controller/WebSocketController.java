package no.dusken.momus.controller;

import no.dusken.momus.model.websocket.Message;
import no.dusken.momus.model.websocket.OutputMessage;
import no.dusken.momus.model.websocket.UserEventMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/disposition/{id}/change")
    @SendTo("/ws/disposition/{id}/changed")
    public OutputMessage sendChange(Message message, @DestinationVariable Long id) throws Exception {
        return new OutputMessage(message.getPageId(), message.getArticleId(), message.getAction(), message.getEditedField(), message.getDate());
    }

    @MessageMapping("/disposition/{id}/user")
    @SendTo("/ws/disposition/{id}/users")
    public UserEventMessage sendUserEvent(UserEventMessage message, @DestinationVariable Long id) throws Exception {
        return message;
    }
}