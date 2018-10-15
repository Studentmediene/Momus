package no.dusken.momus.service.remote;

import no.dusken.momus.service.remotedocument.RemoteDocument;
import no.dusken.momus.service.remotedocument.RemoteDocumentService.ServiceName;

public class RemoteDocumentMock implements RemoteDocument {

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public ServiceName getRemoteServiceName() {
        return ServiceName.MOCK;
	}

}