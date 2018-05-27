package no.dusken.momus.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import no.dusken.momus.model.Messageable;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageContent implements Messageable {
    @JsonProperty private Long publicationId;
    @JsonProperty private Long pageId;
    @JsonProperty private List<Long> articles;
    @JsonProperty private List<Long> adverts;

    @Override
    public List<String> getDestinations() {
        return Collections.singletonList(
                "/ws/publications/" + publicationId + "/page-content"
        );
    }
}
