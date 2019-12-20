package no.dusken.momus.messaging;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

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
