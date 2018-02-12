package no.dusken.momus.ldap;

import no.dusken.momus.authorization.Role;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.PersonRepository;
import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.sql.Blob;
import java.util.*;

public class PersonMapper implements AttributesMapper<Person> {
    private PersonRepository personRepository;
    private boolean active;
    private long lastId;

    private Map<String, Role> groupToRole;

    public PersonMapper(PersonRepository personRepository, boolean active, Map<String, Role> groupToRole) {
        this.personRepository = personRepository;
        this.active = active;
        this.lastId = 0L;
        this.groupToRole = groupToRole;
    }

    @Override
    public Person mapFromAttributes(Attributes attributes) throws NamingException {
        Person person = getPersonFromAttributes(attributes);
        return personRepository.saveAndFlush(person);
    }

    private Person getPersonFromAttributes(Attributes attributes) throws NamingException {
        String username = LDAPAttributes.getUsername(attributes);
        String name = LDAPAttributes.getName(attributes);
        String email = LDAPAttributes.getEmail(attributes);
        String phoneNumber = LDAPAttributes.getPhoneNumber(attributes);
        UUID guid = LDAPAttributes.getGuid(attributes);
        Blob photo = LDAPAttributes.getPhoto(attributes);
        Collection<String> groups = LDAPAttributes.getGroups(attributes);

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

        if(photo != null) {
            person.setPhoto(photo);
        }

        Collection<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        for (String accessGroup: groupToRole.keySet())
            if (groups.contains(accessGroup)) {
                roles.add(groupToRole.get(accessGroup));
        }
        person.setRoles(roles);

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
        while(personRepository.exists(lastId)){ // New person
            lastId++;
        }
        return lastId;
    }
}
