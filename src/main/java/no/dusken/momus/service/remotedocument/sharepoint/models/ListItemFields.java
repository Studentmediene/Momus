package no.dusken.momus.service.remotedocument.sharepoint.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListItemFields {
    public static final String MOMUSLINK_FIELD_NAME = "MomusLink";

    private String id;
    @JsonProperty(MOMUSLINK_FIELD_NAME)
    private String momusLink;
}