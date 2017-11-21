package no.dusken.momus.ldap;

import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.PersonRepository;
import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.UUID;

public class PersonMapper implements AttributesMapper<Person> {
    private PersonRepository personRepository;
    private boolean active;
    private long lastId;

    public PersonMapper(PersonRepository personRepository, boolean active) {
        this.personRepository = personRepository;
        this.active = active;
        this.lastId = 0L;
    }

    @Override
    public Person mapFromAttributes(Attributes attributes) throws NamingException {
        Person person = getPersonFromAttributes(attributes);
        return personRepository.saveAndFlush(person);
    }

    private Person getPersonFromAttributes(Attributes attributes) throws NamingException{
        String username = LDAPAttributes.getUsername(attributes);
        String name = LDAPAttributes.getName(attributes);
        String email = LDAPAttributes.getEmail(attributes);
        String phoneNumber = LDAPAttributes.getPhoneNumber(attributes);
        UUID guid = LDAPAttributes.getGuid(attributes);

        Person person = getPersonIfExists(guid, username);

        if (person == null) {
            person = new Person(findFreeId(), guid, username, name, email, phoneNumber, active);
        } else {
            person.setGuid(guid);
            person.setUsername(username);
            person.setName(name);
            person.setEmail(email);
            person.setPhoneNumber(phoneNumber);
        }

        return person;
    }

    private Person getPersonIfExists(UUID guid, String username) {
        Person person = personRepository.findByGuid(guid);
        if(person == null)
            person = personRepository.findByUsername(username);
        return person;
    }

    /**
     * We have to find id manually since before AD, the user id was directly fetched from LDAP,
     * but with AD there is no simple number field that is unique. We have to retain old user ids to ensure
     * reference integrity of old articles.
     */
    private Long findFreeId(){
        while(personRepository.findOne(lastId) != null){ // New person
            lastId++;
        }
        return lastId;
    }
}
