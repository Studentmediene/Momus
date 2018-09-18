package no.dusken.momus.service.sharepoint.models;

import java.time.LocalDate;

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
public class DriveItem {
    private String id;
    private String name;
    private String webUrl;
    private Entity lastModifiedBy;
    private LocalDate lastModifiedDateTime;
    private FileResource file;
    private FolderResource folder;
}