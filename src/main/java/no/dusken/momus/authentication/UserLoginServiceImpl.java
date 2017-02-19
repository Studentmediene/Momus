/*
 * Copyright 2016 Studentmediene i Trondheim AS
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

import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service for authentication and retrieval of logged in user to be used by other objects.
 */
@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public Long getId() {
        return 11713L;
    }

    @Override
    public String getUsername() {
        return "eivigri";
    }

    @Override
    public boolean login(LdapUserPwd token) {
        return true;
    }

    @Override
    public void logout() {

    }

    @Override
    public Person getLoggedInUser() {
        return personRepository.findOne(getId());
    }

    public AuthUserDetails getLoggedInUserDetails() {
        return null;
    }

    private boolean isAuthenticated(Authentication authentication) {
        return true;
    }

}
