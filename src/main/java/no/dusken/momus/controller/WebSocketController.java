package no.dusken.momus.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import no.dusken.momus.model.websocket.Message;
import no.dusken.momus.model.websocket.OutputMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebSocketController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    
    // @MessageMapping("/disposition/login")
    // public void sendLoggedIn(@Payload Message message, SimpMessageHeaderAccessor  headerAccessor) throws Exception {
    //     String sessionId = headerAccessor.getSessionId();
    //     System.out.println(sessionId);
    //     String time = new SimpleDateFormat("HH:mm").format(new Date());

    //     SimpMessageHeaderAccessor ha = SimpMessageHeaderAccessor
    //         .create(SimpMessageType.MESSAGE);
    //     ha.setSessionId(headerAccessor.getSessionId());
    //     ha.setLeaveMutable(true);
    //     OutputMessage reply = new OutputMessage(message.getFrom(), headerAccessor.getSessionId(), time, "sessionId");
    //     simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/session", reply, ha.getMessageHeaders());
    //     //return new OutputMessage(message.getFrom(), message.getText(), time, message.getAction());
    // }

    // @MessageMapping("/disposition/logout")
    // @SendTo("/ws/disposition/loggedout")
    // public OutputMessage sendLoggedOut(Message message) throws Exception {
    //     String time = new SimpleDateFormat("HH:mm").format(new Date());
    //     return new OutputMessage(message.getFrom(), message.getText(), time, message.getAction());
    // }

    @MessageMapping("/disposition/change")
    @SendTo("/ws/disposition/changed")
    public OutputMessage sendChange(Message message) throws Exception {
        return new OutputMessage(message.getFrom(), "Disposition is updated", message.getDate(), "updated");
    }
}