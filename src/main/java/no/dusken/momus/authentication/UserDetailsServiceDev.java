package no.dusken.momus.authentication;

import no.dusken.momus.model.Person;
import no.dusken.momus.config.MockToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;

public class UserDetailsServiceDev implements UserDetailsService {

    @Autowired
    private AuthenticationManager authenticationManager;

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
