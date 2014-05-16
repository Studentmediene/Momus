/*
 * Copyright 2014 Studentmediene i Trondheim AS
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
import no.dusken.momus.model.Person;
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

import static junit.framework.Assert.assertEquals;

@Transactional // <-- rollback the database after each test
public class UserAuthoritiesTest extends AbstractTestRunner {

    @Autowired
    UserAuthorities userAuthorities;

    @Autowired
    PersonRepository personRepository;

    @Before
    public void setUp() {
        // Some sets of permissions
        Set<String> permissions1 = new HashSet<>();
        permissions1.add("momus:superuser");
        permissions1.add("momus:photographer");

        Set<String> permissions2 = new HashSet<>();
        permissions2.add("momus:journalist");
        permissions2.add("momus:photographer");
        permissions2.add("momus:editor");


        personRepository.save(new Person(1L, null, permissions1, "username", "myFirstName", "myLastName", "mail@mail.com", "12345678", false));
        personRepository.save(new Person(2L, null, permissions2, "testname", "testFirst", "testLast", "test@test.com", "87654321", false));
        personRepository.save(new Person(3L, null, null, "noPermissions", "fail", "noaccess", "yo@giefaccess.com", "81549200", true));
    }

    @Test(expected = RestException.class)
    public void testNonExistingPerson() {
        userAuthorities.getAuthoritiesForUser(4L);
    }

    @Test
    public void testAuthoritiesTwo() {
        AuthUserDetails authoritiesForUser = userAuthorities.getAuthoritiesForUser(1L);

        Collection<? extends GrantedAuthority> authorities = authoritiesForUser.getAuthorities();
        assertEquals(2, authorities.size());

        Collection<GrantedAuthority> expectedAuthorities = new HashSet<>();
        expectedAuthorities.add(new SimpleGrantedAuthority("momus:superuser"));
        expectedAuthorities.add(new SimpleGrantedAuthority("momus:photographer"));

        assertEquals(expectedAuthorities, authorities);
    }

    @Test
    public void testAuthoritiesThree() {
        AuthUserDetails authoritiesForUser = userAuthorities.getAuthoritiesForUser(2L);

        Collection<? extends GrantedAuthority> authorities = authoritiesForUser.getAuthorities();
        assertEquals(3, authorities.size());

        Collection<GrantedAuthority> expectedAuthorities = new HashSet<>();
        expectedAuthorities.add(new SimpleGrantedAuthority("momus:journalist"));
        expectedAuthorities.add(new SimpleGrantedAuthority("momus:photographer"));
        expectedAuthorities.add(new SimpleGrantedAuthority("momus:editor"));

        assertEquals(expectedAuthorities, authorities);
    }

    @Test
    public void testAuthoritiesNotFailWhenNone() {
        AuthUserDetails authoritiesForUser = userAuthorities.getAuthoritiesForUser(3L);

        Collection<? extends GrantedAuthority> authorities = authoritiesForUser.getAuthorities();
        assertEquals(0, authorities.size());
    }
}
