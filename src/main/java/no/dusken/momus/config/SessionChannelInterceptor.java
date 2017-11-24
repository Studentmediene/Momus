package no.dusken.momus.config;

import java.security.Principal;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

public class SessionChannelInterceptor extends ChannelInterceptorAdapter {

@Override
public Message<?> preSend(Message<?> message, MessageChannel channel) {

    MessageHeaders headers = message.getHeaders();
    SimpMessageType type = (SimpMessageType) headers.get("simpMessageType");
    String simpSessionId = (String) headers.get("simpSessionId");

    if (type == SimpMessageType.CONNECT) {
        Principal principal = (Principal) headers.get("simpUser");
        System.out.println("WsSession " + simpSessionId + " is connected for user " + principal.getName());
    } else if (type == SimpMessageType.DISCONNECT) {
        System.out.println("WsSession " + simpSessionId + " is disconnected");
    }
    return message;
}
}