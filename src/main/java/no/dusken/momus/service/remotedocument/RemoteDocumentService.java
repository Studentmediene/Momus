package no.dusken.momus.service.remotedocument;

import java.io.IOException;

import no.dusken.momus.model.Article;

public interface RemoteDocumentService {
    enum ServiceName {
        GOOGLE_DRIVE, SHAREPOINT, MOCK
    }
    void setup();

    /**
     * @return The ID of the created document
     * @throws IOException
     */
    RemoteDocument createDocument(String name) throws IOException;

    void addRemoteMetadata(Article article);

    ServiceName getServiceName();
}