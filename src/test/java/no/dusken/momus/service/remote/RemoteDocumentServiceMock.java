package no.dusken.momus.service.remote;

import no.dusken.momus.model.Article;
import no.dusken.momus.service.remotedocument.RemoteDocument;
import no.dusken.momus.service.remotedocument.RemoteDocumentService;

public class RemoteDocumentServiceMock implements RemoteDocumentService {

    @Override
    public void setup() {
    }

    @Override
    public RemoteDocument createDocument(String name) {
        return new RemoteDocumentMock();        
    }

    @Override
    public ServiceName getServiceName() {
        return ServiceName.MOCK;
    }

    @Override
    public void addRemoteMetadata(Article article) {

	}

}