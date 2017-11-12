package no.dusken.momus.ldap;

import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.PersonRepository;
import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.nio.ByteBuffer;
import java.util.Optional;
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

    private String getAttribute(Attributes attributes, String... keys) throws NamingException {
        Attribute attribute;
        for (String key : keys) {
            attribute = attributes.get(key);
            if (attribute != null) {
                return (String) attribute.get();
            }
        }
        return "";
    }

    private UUID getGuidFromBytes(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        return new UUID(high, low);
    }

    private String getName(Attributes attributes) throws NamingException {
        String name = getAttribute(attributes, "displayName");
        if (name.trim().isEmpty()) {
            name = String.format(
                    "%s %s",
                    getAttribute(attributes, "givenName"),
                    getAttribute(attributes, "sn"));
            if (name.trim().isEmpty()) {
                name = String.format(
                        "%s (mangler visningsnavn)",
                        getAttribute(attributes, "username"));
            }
        }
        return name;
    }

    private Optional<Person> getPersonIfExists(UUID guid, String username) {
        Person person = personRepository.findByGuid(guid);

        if(person == null)
            person = personRepository.findByUsername(username);

        if(person == null)
            return Optional.empty();

        return Optional.of(person);
    }

    private Person updatePerson(Person person, UUID guid, String username, String email, String phoneNumber) {
        person.setGuid(guid);
        person.setUsername(username);
        person.setEmail(email);
        person.setPhoneNumber(phoneNumber);
        return person;
    }

    private Person getPersonFromAttributes(Attributes attributes) throws NamingException{
        String username = getAttribute(attributes, "sAMAccountName");
        String name = getName(attributes);
        String email = getAttribute(attributes, "mail", "otherMailbox");
        String phoneNumber = getAttribute(attributes, "telephoneNumber");
        UUID guid = getGuidFromBytes((byte[]) attributes.get("objectGUID").get());

        Optional<Person> person = getPersonIfExists(guid, username);
        if(person.isPresent()) {
            return updatePerson(person.get(), guid, username, email, phoneNumber);
        }

        System.out.println(String.format("Did not find %s in db ", username));

        Long id = findFreeId();

        return new Person(id, guid, username, name, email, phoneNumber, active);
    }

    @Override
    public Person mapFromAttributes(Attributes attributes) throws NamingException {
        Person person = getPersonFromAttributes(attributes);
        return personRepository.saveAndFlush(person);

    }

    private Long findFreeId(){
        while(personRepository.findOne(lastId) != null){ // New person
            lastId++;
        }
        return lastId;
    }
}
