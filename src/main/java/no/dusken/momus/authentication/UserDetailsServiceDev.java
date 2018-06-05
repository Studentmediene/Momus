package no.dusken.momus.authentication;

import no.dusken.momus.model.Person;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;

public class UserDetailsServiceDev implements UserDetailsService{
    @Override
    public Person getLoggedInPerson() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            return (Person) authentication.getPrincipal();
        }
        return null;
    }

    @Override
    public Object loadUserBySAML(SAMLCredential samlCredential) throws UsernameNotFoundException {
        return null;
    }
}
