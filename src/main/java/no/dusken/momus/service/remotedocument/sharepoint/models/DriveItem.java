package no.dusken.momus.service.remotedocument.sharepoint.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.dusken.momus.service.remotedocument.RemoteDocument;
import no.dusken.momus.service.remotedocument.RemoteDocumentService.ServiceName;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = "id")
public class DriveItem implements RemoteDocument {
    private String id;
    private String name;
    private String webUrl;
    private Entity lastModifiedBy;
    private LocalDate lastModifiedDateTime;
    private FileResource file;
    private FolderResource folder;
    private Object deleted;
    private ListItem listItem;

    @JsonIgnore
    public String getUrl() {
        return webUrl;
    }

    @JsonIgnore
    public String getLastModifiedUserId() {
        if(lastModifiedBy.getUser() == null) {
            return null;
        }
        return lastModifiedBy.getUser().getId();
    }

    @Override
    @JsonIgnore
    public ServiceName getRemoteServiceName() {
        return ServiceName.SHAREPOINT;
	}
}