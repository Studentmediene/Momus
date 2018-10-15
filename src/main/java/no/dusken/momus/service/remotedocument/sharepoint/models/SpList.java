package no.dusken.momus.service.remotedocument.sharepoint.models;

import java.util.List;

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
public class SpList {
    private String id;
    private String name;
    private List<ListColumn> columns;
    private DriveItem drive;
}