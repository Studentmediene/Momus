package no.dusken.momus.service.remotedocument;

public interface RemoteDocumentService {
    void setup();

    /**
     * @return The ID of the created document
     */
    RemoteDocument createDocument(String name);

    String getServiceName();
}