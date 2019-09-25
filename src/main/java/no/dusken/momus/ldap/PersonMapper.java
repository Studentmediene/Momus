package no.dusken.momus.ldap;

import no.dusken.momus.authorization.Role;
import no.dusken.momus.model.Avatar;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.AvatarRepository;
import no.dusken.momus.service.repository.PersonRepository;
import org.springframework.ldap.core.AttributesMapper;

import lombok.Getter;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.sql.Blob;
import java.util.*;

public class PersonMapper implements AttributesMapper<Person> {
    private PersonRepository personRepository;
    private AvatarRepository avatarRepository;
    private boolean active;
    private long lastId;

    private List<GroupRole> groupToRole;

    public PersonMapper(PersonRepository personRepository, AvatarRepository avatarRepository, boolean active, List<GroupRole> groupToRole) {
        this.personRepository = personRepository;
        this.avatarRepository = avatarRepository;
        this.active = active;
        this.lastId = 0L;
        this.groupToRole = groupToRole;
    }

    @Override
    public Person mapFromAttributes(Attributes attributes) throws NamingException {
        // Create person from ldap attributes
        Person person = getPersonFromAttributes(attributes);
        // Persist it
        person = personRepository.saveAndFlush(person);

        // Get the avatar (photo) if it exists
        Optional<Avatar> avatar = getAvatarFromAttributes(person, attributes);
        avatar.ifPresent((a) -> avatarRepository.saveAndFlush(a));

        return person;
    }

    private Optional<Avatar> getAvatarFromAttributes(Person person, Attributes attributes) throws NamingException {
        Blob blob = LDAPAttributes.getAvatar(attributes);
        if (blob != null) {
            return Optional.of(Avatar.builder().id(person.getId()).avatar(blob).build());
        }
        return Optional.empty();
    }

    private Person getPersonFromAttributes(Attributes attributes) throws NamingException {
        String username = LDAPAttributes.getUsername(attributes);
        String name = LDAPAttributes.getName(attributes);
        String email = LDAPAttributes.getEmail(attributes);
        String phoneNumber = LDAPAttributes.getPhoneNumber(attributes);
        UUID guid = LDAPAttributes.getGuid(attributes);
        Collection<String> groups = LDAPAttributes.getGroups(attributes);

        Person person = getPersonIfExists(guid, username);

        if (person == null) {
            person = Person.builder()
                    .id(findFreeId())
                    .guid(guid)
                    .username(username)
                    .name(name)
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .active(active)
                    .build();
        } else {
            person.setGuid(guid);
            person.setUsername(username);
            person.setName(name);
            person.setEmail(email);
            person.setPhoneNumber(phoneNumber);
            person.setActive(active);
        }

        Collection<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        for (GroupRole accessGroup: groupToRole)
            if (isMemberOf(groups, accessGroup.getGroup())) {
                roles.add(accessGroup.getRole());
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
        while(personRepository.existsById(lastId)){ // New person
            lastId++;
        }
        return lastId;
    }

    private boolean isMemberOf(Collection<String> userGroups, String group) {
        return userGroups.stream().anyMatch(g -> g.toLowerCase().startsWith(group.toLowerCase()));
    }

    @Getter
    enum Status {
        ACTIVE("OU=Brukere"),
        INACTIVE("OU=Sluttede"),
        TEMP_LEAVE("OU=Permisjon,OU=Brukere");

        Status(String base) {
            this.base = base;
        }

        private final String base;
    }
}
