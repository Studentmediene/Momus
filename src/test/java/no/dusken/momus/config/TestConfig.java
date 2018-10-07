package no.dusken.momus.config;

import no.dusken.momus.service.MessagingService;
import no.dusken.momus.service.remote.RemoteDocumentServiceMock;
import no.dusken.momus.service.remotedocument.RemoteDocumentService;

import org.mockito.Mockito;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import no.dusken.momus.authentication.UserDetailsService;
import no.dusken.momus.authentication.UserDetailsServiceMock;

@Configuration
@Import(value = {ApplicationConfig.class, SecurityConfig.class})
@PropertySource(value = {"classpath:momus.properties", "classpath:test.properties"})
public class TestConfig extends WebMvcConfigurerAdapter {

    @Bean(name = "userDetailsService")
    public UserDetailsService userDetailsServiceImpl() {
        return new UserDetailsServiceMock();
    }

    @Bean
    @Primary
    public MessagingService messagingService() {
        return Mockito.mock(MessagingService.class);
    }

    @Bean
    @Primary
    public RemoteDocumentService remoteDocumentService() {
        return new RemoteDocumentServiceMock();
    }
}