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

import no.dusken.momus.model.Person;

/**
 * A mock of the service that works when testing (since there is no "logged in user" then)
 * Always returns id 1 as the logged in user
 */
public class UserLoginServiceMock implements UserLoginService {


    @Override
    public Long getId() {
        return 1L;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean login(SmmDbToken token) {
        return false;
    }

    @Override
    public void logout() {
        // nothing
    }

    @Override
    public Person getLoggedInUser() {
        return null;
    }
}
