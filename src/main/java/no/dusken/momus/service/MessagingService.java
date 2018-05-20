package no.dusken.momus.service;

import no.dusken.momus.model.Messageable;
import no.dusken.momus.model.websocket.Action;
import no.dusken.momus.model.websocket.EntityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;

@Service
public class MessagingService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void broadcastEntityAction(Messageable entity, Action action) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String messageId = attributes.getRequest().getHeader("X-MOM-SENDER");

        HashMap<String, Object> headers = new HashMap<>();
        headers.put("message-sender", messageId);
        entity.getDestinations().forEach(destination ->
                messagingTemplate.convertAndSend(destination, new EntityMessage(entity, action), headers)
        );
    }
}
