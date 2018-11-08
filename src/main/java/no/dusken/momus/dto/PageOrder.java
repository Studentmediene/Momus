package no.dusken.momus.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import no.dusken.momus.model.Messageable;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageOrder implements Messageable {
    @JsonProperty private Long publicationId;
    @JsonProperty private List<PageId> order;

    @Override
    @JsonIgnore
    public List<String> getDestinations() {
        return Collections.singletonList(
                "/ws/publications/" + publicationId + "/page-order"
        );
    }
}
