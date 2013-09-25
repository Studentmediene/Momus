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
public class UserLoginService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Will not touch the database
     * @return Id of the currently logged in user
     */
    public Long getId() {
        return getLoggedInUserDetails().getId();
    }

    /**
     * Will not use the database
     * @return Username of the currently logged in user
     */
    public String getUsername() {
        return getLoggedInUserDetails().getUsername();
    }

    /**
     * Try to auth a user based on a token
     * @param token
     * @return whether the login succeeded or not.
     */
    public boolean login(SmmAbToken token) {
        Authentication authentication = authenticationManager.authenticate(new Token(token));
        boolean isAuthenticated = isAuthenticated(authentication);
        if (isAuthenticated) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        return isAuthenticated;
    }

    /**
     * Logs out the current user
     */
    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    /**
     * A proper Person object of the logged in user.
     * Should only be called when needed, as it touches the database.
     * Calling this method multiple times will touch the database multiple times, so put the returned Person in a local variable instead!
     */
    public Person getLoggedInUser() {
        return personRepository.findOne(getId());
    }

    public AuthUserDetails getLoggedInUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(authentication)) {
            return (AuthUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }

}
