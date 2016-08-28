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

package no.dusken.momus.controller;

import no.dusken.momus.authentication.LdapUserPwd;
import no.dusken.momus.authentication.UserLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserLoginService userLoginService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody void login(@RequestBody LdapUserPwd ldapUserPwd) {
        logger.debug("Login try with: {}", ldapUserPwd);
        userLoginService.login(ldapUserPwd);
    }

    @RequestMapping("/logout")
    public @ResponseBody void logout() {
        logger.info("User with id {} is logging out", userLoginService.getId());
        userLoginService.logout();
    }

}
