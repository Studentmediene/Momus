package no.dusken.momus.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class UserEventMessage {
    private UserAction userAction;
    private Long userid;
    private String state;
}
