package no.dusken.momus.service.remotedocument.sharepoint.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDelta {
    private String id;
    private String name;
    private Object file;
    private Object deleted;
}