package no.dusken.momus.model.websocket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import no.dusken.momus.model.Messageable;

import java.time.ZonedDateTime;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@Getter
public class EntityMessage {

    @JsonProperty
    private ZonedDateTime timestamp;

    @JsonProperty
    private Action action;

    @JsonProperty
    private Messageable entity;

    public EntityMessage(Messageable entity, Action action) {
        this.timestamp = ZonedDateTime.now();
        this.entity = entity;
        this.action = action;
    }
}
