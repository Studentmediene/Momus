package no.dusken.momus.common.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import no.dusken.momus.messaging.MessagingService;
import no.dusken.momus.person.authentication.UserDetailsService;
import no.dusken.momus.person.authentication.UserDetailsServiceMock;

@Configuration
@Import(value = {ApplicationConfig.class, SecurityConfig.class})
@PropertySource(value = {"classpath:momus.properties", "classpath:test.properties"})
public class TestConfig extends WebMvcConfigurationSupport {

    @Bean(name = "userDetailsService")
    public UserDetailsService userDetailsServiceImpl() {
        return new UserDetailsServiceMock();
    }

    @Bean
    @Primary
    public MessagingService messagingService() {
        return Mockito.mock(MessagingService.class);
    }
}