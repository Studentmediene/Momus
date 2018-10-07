package no.dusken.momus.service.remote;

import no.dusken.momus.service.remotedocument.RemoteDocument;
import no.dusken.momus.service.remotedocument.RemoteDocumentService;

public class RemoteDocumentServiceMock implements RemoteDocumentService {

    @Override
    public void setup() {
    }

    @Override
    public RemoteDocument createDocument(String name) {
        System.out.println("Creating document!");
        return new RemoteDocument(){
        
            @Override
            public String getUrl() {
                return name;
            }
        
            @Override
            public String getId() {
                return name;
            }
        };
    }

    @Override
    public ServiceName getServiceName() {
        return ServiceName.MOCK;
	}

}