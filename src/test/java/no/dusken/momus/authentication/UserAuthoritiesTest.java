/*
 * Copyright 2013 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.dusken.momus.authentication;

import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.Group;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.GroupRepository;
import no.dusken.momus.service.repository.PersonRepository;
import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.*;

@Transactional
public class UserAuthoritiesTest extends AbstractTestRunner {

    @Autowired
    UserAuthorities userAuthorities;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    PersonRepository personRepository;

    @Before
    public void setUp() {
        // Add some data
        Group adminGroup = groupRepository.save(new Group(1L, "Admin"));
        Group photographerGroup = groupRepository.save(new Group(2L, "Photographer"));

        Set<Group> groups1 = new HashSet<>();
        groups1.add(adminGroup);

        Set<Group> groups2 = new HashSet<>();
        groups2.add(adminGroup);
        groups2.add(photographerGroup);

        personRepository.save(new Person(1L, groups1, "username", "myFirstName", "myLastName", "mail@mail.com", "12345678"));
        personRepository.save(new Person(2L, groups2, "testname", "testFirst", "testLast", "test@test.com", "87654321"));
    }

    @Test(expected = RestException.class)
    public void testNonExistingPerson() {
        userAuthorities.getAuthoritiesForUser(3L);
    }

    @Test
    public void testAuthoritiesAdmin() {
        AuthUserDetails authoritiesForUser = userAuthorities.getAuthoritiesForUser(1L);

        Collection<? extends GrantedAuthority> authorities = authoritiesForUser.getAuthorities();
        assertEquals(1, authorities.size());

        Collection<GrantedAuthority> expectedAuthorities = new HashSet<>();
        expectedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        assertEquals(expectedAuthorities, authorities);
    }

    @Test
    public void testAuthoritiesAdminAndPhotographer() {
        AuthUserDetails authoritiesForUser = userAuthorities.getAuthoritiesForUser(2L);

        Collection<? extends GrantedAuthority> authorities = authoritiesForUser.getAuthorities();
        assertEquals(2, authorities.size());

        Collection<GrantedAuthority> expectedAuthorities = new HashSet<>();
        expectedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        expectedAuthorities.add(new SimpleGrantedAuthority("ROLE_PHOTOGRAPHER"));

        assertEquals(expectedAuthorities, authorities);
    }
}
