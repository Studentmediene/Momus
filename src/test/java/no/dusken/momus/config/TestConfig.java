package no.dusken.momus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import no.dusken.momus.authentication.UserDetailsService;
import no.dusken.momus.authentication.UserDetailsServiceMock;

@Configuration
@Import(value = {ApplicationConfig.class, SecurityConfig.class})
@PropertySource(value = {"classpath:momus.properties", "classpath:test.properties"})
public class TestConfig extends WebMvcConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsServiceImpl() {
        return new UserDetailsServiceMock();
    }
}