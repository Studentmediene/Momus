package no.dusken.momus.person.authentication;

import no.dusken.momus.common.config.MockToken;
import no.dusken.momus.person.Person;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;

public class UserDetailsServiceDev implements UserDetailsService {

    private final AuthenticationManager authenticationManager;

    public UserDetailsServiceDev(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Person getLoggedInPerson() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getClass().equals(AnonymousAuthenticationToken.class)) {
            authentication = authenticationManager.authenticate(new MockToken("eivigri"));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        return (Person) authentication.getPrincipal();
    }

    @Override
    public Object loadUserBySAML(SAMLCredential samlCredential) throws UsernameNotFoundException {
        return null;
    }
}
