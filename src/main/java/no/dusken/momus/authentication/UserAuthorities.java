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
import no.dusken.momus.service.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class to build an AuthUserDetails with the proper authorities
 */
@Service
public class UserAuthorities {

    @Autowired
    private PersonRepository personRepository;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    public AuthUserDetails getAuthoritiesForUser(Long id) {
        Person user = personRepository.findOne(id);

        if (user == null) {
            logger.warn("Couldn't fetch authorities for user {}, got null", id);
            throw new RestException("User not found", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        Set<String> permissions = user.getPermissions();

        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<>();
        if (permissions != null) {
            for (String permission : permissions) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission));
            }
        }

        AuthUserDetails authUserDetails = new AuthUserDetails(user, grantedAuthorities);
        return authUserDetails;
    }
}
