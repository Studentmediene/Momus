package no.dusken.momus.service.remotedocument;

public interface RemoteDocumentService {
    enum ServiceName {
        GOOGLE_DRIVE, SHAREPOINT, MOCK
    }
    void setup();

    /**
     * @return The ID of the created document
     */
    RemoteDocument createDocument(String name);

    ServiceName getServiceName();
}