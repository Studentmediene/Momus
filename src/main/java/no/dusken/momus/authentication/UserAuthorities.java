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

import no.dusken.momus.model.Group;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper class to build an AuthUserDetails with the proper authorities
 */
@Service
public class UserAuthorities {

    @Autowired
    private PersonRepository personRepository;

    @Transactional
    public AuthUserDetails getAuthoritiesForUser(Long id) {
        Person user = personRepository.findOne(id);
        Set<Group> groups = user.getGroups();

        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Group group : groups) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + group.getName().toUpperCase()));
        }

        AuthUserDetails authUserDetails = new AuthUserDetails(user, grantedAuthorities);
        return authUserDetails;
    }
}
