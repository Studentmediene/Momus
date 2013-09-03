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

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Our custom Authentication, will hold the authorities of the user and the authUserDetails.
 */
public class Token extends AbstractAuthenticationToken {

    private SmmdbToken smmdbToken;
    private UserDetails principal;

    public Token(SmmdbToken smmdbToken) {
        super(null);
        this.smmdbToken = smmdbToken;
        setAuthenticated(false);
    }

    public Token(SmmdbToken smmdbToken, AuthUserDetails principal) {
        super(principal.getAuthorities());
        this.smmdbToken = smmdbToken;
        this.principal = principal;
        setAuthenticated(true);
    }

    public SmmdbToken getSmmdbToken() {
        return smmdbToken;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
