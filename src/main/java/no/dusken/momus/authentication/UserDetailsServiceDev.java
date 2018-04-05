package no.dusken.momus.authentication;

import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;

public class UserDetailsServiceDev implements UserDetailsService{

    public static long LOGGED_IN_USER = 0L;

    private final PersonRepository personRepository;

    @Autowired
    public UserDetailsServiceDev(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Person getLoggedInPerson() {
        return personRepository.findOne(LOGGED_IN_USER);
    }

    @Override
    public Object loadUserBySAML(SAMLCredential samlCredential) throws UsernameNotFoundException {
        return null;
    }
}
