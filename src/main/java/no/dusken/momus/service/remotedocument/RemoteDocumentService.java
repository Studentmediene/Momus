package no.dusken.momus.service.remotedocument;

import java.io.IOException;

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

    ServiceName getServiceName();
}