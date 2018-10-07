package no.dusken.momus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.dusken.momus.service.remotedocument.RemoteDocumentService;
import no.dusken.momus.service.remotedocument.sharepoint.SharepointService;

@Configuration
public class RemoteDocumentConfig {
    @Bean
    public RemoteDocumentService remoteDocumentService(SharepointService sharepointService) {
        return sharepointService;
    }

}