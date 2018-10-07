package no.dusken.momus.service.remotedocument.sharepoint.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDeltaList {
    private List<DriveItem> value;

    @JsonProperty("@odata.nextLink")
    private String nextLink;

    @JsonProperty("@odata.deltaLink")
    private String deltaLink;
}