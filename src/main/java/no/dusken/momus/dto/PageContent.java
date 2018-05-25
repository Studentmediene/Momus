package no.dusken.momus.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@JsonDeserialize(builder = PageContent.PageContentBuilder.class)
public class PageContent {
    @JsonProperty List<Long> articles;
    @JsonProperty List<Long> adverts;
}
