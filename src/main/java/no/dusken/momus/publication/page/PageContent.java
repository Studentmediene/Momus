package no.dusken.momus.publication.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import no.dusken.momus.messaging.Messageable;

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
    @JsonIgnore
    public List<String> getDestinations() {
        return Collections.singletonList(
                "/ws/publications/" + publicationId + "/page-content"
        );
    }
}
