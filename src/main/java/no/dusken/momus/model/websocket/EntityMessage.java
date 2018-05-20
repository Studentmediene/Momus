package no.dusken.momus.model.websocket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import no.dusken.momus.model.AbstractEntity;
import no.dusken.momus.model.Article;
import no.dusken.momus.model.Messageable;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class EntityMessage {

    @JsonProperty
    protected ZonedDateTime timestamp;

    @JsonProperty
    protected Action action;

    @JsonProperty
    @Getter
    protected Messageable entity;

    public EntityMessage(Messageable entity, Action action) {
        this.timestamp = ZonedDateTime.now();
        this.entity = entity;
        this.action = action;
    }
}
