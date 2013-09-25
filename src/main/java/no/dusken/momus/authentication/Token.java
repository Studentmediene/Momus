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
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Our custom Authentication, will hold the authorities of the user and the authUserDetails.
 */
public class Token extends AbstractAuthenticationToken {

    private SmmAbToken smmDbToken;
    private UserDetails principal;

    /**
     * Standard, non-authenticated token
     */
    public Token(SmmAbToken smmDbToken) {
        super(null);
        this.smmDbToken = smmDbToken;
        setAuthenticated(false);
    }

    /**
     * When the user has been verified
     */
    public Token(SmmAbToken smmDbToken, AuthUserDetails principal) {
        super(principal.getAuthorities());
        this.smmDbToken = smmDbToken;
        this.principal = principal;
        setAuthenticated(true);
    }

    public SmmAbToken getSmmDbToken() {
        return smmDbToken;
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
