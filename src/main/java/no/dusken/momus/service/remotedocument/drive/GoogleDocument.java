package no.dusken.momus.service.remotedocument.drive;

import com.google.api.services.drive.model.File;

import no.dusken.momus.service.remotedocument.RemoteDocument;
import no.dusken.momus.service.remotedocument.RemoteDocumentService.ServiceName;

public class GoogleDocument implements RemoteDocument {
    private File file;

    public GoogleDocument(File file) {
        this.file = file;
    }

    @Override
    public String getId() {
        return file.getId();
    }

    @Override
    public String getUrl() {
        return "https://docs.google.com/document/d/" + file.getId() + "/edit";
    }

    @Override
    public ServiceName getRemoteServiceName() {
        return ServiceName.GOOGLE_DRIVE;
	}

}