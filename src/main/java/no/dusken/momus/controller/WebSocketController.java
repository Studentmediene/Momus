package no.dusken.momus.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import no.dusken.momus.model.websocket.Message;
import no.dusken.momus.model.websocket.OutputMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/disposition")
    @SendTo("/ws/disposition/messages")
    public OutputMessage send(Message message) throws Exception {
        System.out.println(message.getFrom().getFullName());
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time, message.getAction());
    }
}