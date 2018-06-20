package no.dusken.momus.service;

import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.model.Messageable;
import no.dusken.momus.model.websocket.Action;
import no.dusken.momus.model.websocket.EntityMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;

@Service
@Slf4j
public class MessagingService {

    private final SimpMessagingTemplate messagingTemplate;

    public MessagingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastEntityAction(Messageable entity, Action action) {
        log.debug("Broadcasting {} and action {}", entity, action);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String messageId = attributes.getRequest().getHeader("X-MOM-SENDER");

        HashMap<String, Object> headers = new HashMap<>();
        headers.put("message-sender", messageId);
        entity.getDestinations().forEach(destination ->
                messagingTemplate.convertAndSend(destination, new EntityMessage(entity, action), headers)
        );
    }
}
