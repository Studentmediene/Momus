package no.dusken.momus.common.config;

import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.person.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("noAuth")
@Slf4j
public class NoSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PersonRepository personRepository;

    @Autowired
    public NoSecurityConfig(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("Auth disabled, not setting up security");
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .antMatchers("/api/dev/**").permitAll()
                .and().exceptionHandling().authenticationEntryPoint(myEntryPoint());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                MockToken token = (MockToken) authentication;
                return new MockToken(token.getUsername(), personRepository.findByUsername(token.getUsername()));
            }

            @Override
            public boolean supports(Class<?> aClass) {
                return aClass.equals(MockToken.class);
            }
        });
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint myEntryPoint() {
        return (httpServletRequest, httpServletResponse, e) -> httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
