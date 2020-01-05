package no.dusken.momus.person.ldap;

import org.springframework.core.env.Environment;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.stereotype.Service;

import lombok.Getter;
import no.dusken.momus.person.*;
import no.dusken.momus.person.authorization.Role;
import no.dusken.momus.section.SectionService;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.sql.Blob;
import java.util.*;

@Service
public class PersonMapper {
    private final Environment env;

    private final PersonRepository personRepository;
    private final AvatarRepository avatarRepository;
    private final SectionService sectionService;

    private long highestUsedId;

    public PersonMapper(Environment env, PersonRepository personRepository, AvatarRepository avatarRepository, SectionService sectionService) {
        this.env = env;
        this.personRepository = personRepository;
        this.avatarRepository = avatarRepository;
        this.sectionService = sectionService;
        this.highestUsedId = 0L;
    }

    public AttributesMapper<Person> createMapper(boolean activeStatus) {
        return new AttributesMapper<Person>() {

            @Override
            public Person mapFromAttributes(Attributes attributes) throws NamingException {
                // Create person from ldap attributes
                Person person = getPersonFromAttributes(attributes, activeStatus);
                // Persist it
                person = personRepository.saveAndFlush(person);

                // Get the avatar (photo) if it exists
                Optional<Avatar> avatar = getAvatarFromAttributes(person, attributes);
                avatar.ifPresent((a) -> avatarRepository.saveAndFlush(a));

                return person;
            }
        };
    }

    private Optional<Avatar> getAvatarFromAttributes(Person person, Attributes attributes) throws NamingException {
        Blob blob = LDAPAttributes.getAvatar(attributes);
        if (blob != null) {
            return Optional.of(Avatar.builder().id(person.getId()).avatar(blob).build());
        }
        return Optional.empty();
    }

    private Person getPersonFromAttributes(Attributes attributes, boolean active) throws NamingException {
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
        for (GroupRole accessGroup: GroupRole.getGroupRoleMapping(env))
            if (isMemberOf(groups, accessGroup.getGroup())) {
                roles.add(accessGroup.getRole());
        }
        person.setRoles(roles);

        Role mainRole = getMainRole(roles);
        person.setMainRole(mainRole);
        person.setSection(sectionService.getSectionForRole(mainRole).orElse(null));

        return person;
    }

    /**
     * The "main role" of a person is the role they have that appears first in the Role enum
     */
    private Role getMainRole(Collection<Role> roles) {
        for (Role r : Role.values()) {
            if (roles.contains(r)) {
                return r;
            }
        }

        return null;
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
        while(personRepository.existsById(highestUsedId)){ // New person
            highestUsedId++;
        }
        return highestUsedId;
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
