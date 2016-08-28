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

public interface UserLoginService {
    /**
     * Will not touch the database
     * @return Id of the currently logged in user
     */
    Long getId();

    /**
     * Will not use the database
     * @return Username of the currently logged in user
     */
    String getUsername();

    /**
     * Try to auth a user based on ldap credentials
     * @param token
     * @return whether the login succeeded or not.
     */
    boolean login(LdapUserPwd token);

    /**
     * Logs out the current user
     */
    void logout();

    /**
     * A proper Person object of the logged in user.
     * Should only be called when needed, as it touches the database.
     * Calling this method multiple times will touch the database multiple times, so put the returned Person in a local variable instead!
     */
    Person getLoggedInUser();
}
