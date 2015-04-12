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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

/**
 * Will try to authenticate a user based on a token, and if the token is valid it will
 * create a new token holding the proper authorities and authUserDetails
 */
@Service
public class TokenAuthenticationProvider implements AuthenticationProvider {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    LdapTemplate ldapTemplate;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Token token = (Token) authentication;
        LdapUserPwd ldapUserPwd = token.getLdapUserPwd();
        
        if (validateLogin(ldapUserPwd)) {
            Person loggedInUser = getLoggedInUser(ldapUserPwd.getUsername());
            AuthUserDetails authUserDetails = new AuthUserDetails(loggedInUser);

            // Return an updated token with the right user details
            return new Token(ldapUserPwd, authUserDetails);
        }

        throw new BadCredentialsException("Invalid username or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(Token.class);
    }

    private boolean validateLogin(LdapUserPwd ldapUserPwd) {

        boolean authenticate = ldapTemplate.authenticate("ou=Users", "(uid=" + ldapUserPwd.getUsername() + ")" , ldapUserPwd.getPassword());

        if (authenticate) {
            logger.info("User with username {} was authenticated by LDAP", ldapUserPwd.getUsername());
        } else {
            logger.error("Wrong username or password for user {}", ldapUserPwd.getUsername());
        }

        return authenticate;
    }

    private Person getLoggedInUser(String username) {
        Person person = personRepository.findByUsername(username);
        if (person == null) {
            logger.error("User was logged in, but not found in our database: {}", username);
            throw new RestException("User was logged in, but not found in our database!", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return person;
    }


}
