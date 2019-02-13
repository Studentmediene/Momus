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

package no.dusken.momus.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.util.IOUtils;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import no.dusken.momus.authentication.UserDetailsService;
import no.dusken.momus.authorization.AdminAuthorization;
import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.Avatar;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.PersonService;

@RestController
@Transactional
@RequestMapping("/api/person")
public class PersonController {
    private final PersonService personService;
    private final UserDetailsService userDetailsService;

    public PersonController(PersonService personService, UserDetailsService userDetailsService) {
        this.personService = personService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Gets all active persons. In addition, if article ids are supplied, will
     * return all contributors on those even if they are inactive
     */
    @GetMapping
    public Set<Person> getActivePersons(@RequestParam(
            value="articleIds", required = false, defaultValue = "") List<Long> articleIds) {
        return personService.getActivePersonsAndArticleContributors(articleIds);
    }

    @GetMapping("/{id}")
    public Person getPersonById(@PathVariable("id") Long id) {
        return personService.getPersonById(id);
    }

    @GetMapping("/{id}/photo")
    public void getPersonPhoto(@PathVariable("id") Long id, HttpServletResponse response) throws IOException, SQLException {
        Avatar avatar = personService.getPersonPhoto(id);

        response.addHeader("Content-Type", "image/jpeg");

        ServletOutputStream outStream = response.getOutputStream();
        IOUtils.copy(avatar.getAvatar().getBinaryStream(), outStream);
        outStream.flush();
        outStream.close();
    }

    @GetMapping("/me")
    public Person getCurrentUser() {
        System.out.println("getting");
        return userDetailsService.getLoggedInPerson();
    }

    @GetMapping("/me/photo")
    public void getCurrentUserPhoto(HttpServletResponse response) throws IOException, SQLException {
        getPersonPhoto(userDetailsService.getLoggedInPerson().getId(), response);
    }

    @PatchMapping("/me/favouritesection")
    public Person updateFavouritesection(@RequestParam Long section) {
        return personService.updateFavouritesection(userDetailsService.getLoggedInPerson(), section);
    }

    @GetMapping("/loggedin/all")
    @AdminAuthorization
    public Set<Person> getAllActivePersons() {
        return personService.getAllLoggedInPersons();
    }

    @GetMapping("/loggedin")
    public Set<Person> getActivePersonsAtState(@RequestParam String state) {
        return personService.getLoggedInPersonsAtState(state);
    }

    @PutMapping("/sessions/{sessid}")
    public void setStateOfSession(@PathVariable String sessid, @RequestParam String state) {
        personService.setStateForSession(sessid, state);
    }

    @EventListener
    public void handleSessionStart(SessionConnectedEvent connectedEvent) {
        String sessionId = StompHeaderAccessor.wrap(connectedEvent.getMessage()).getSessionId();
        personService.startSessionForPerson(sessionId, connectedEvent.getUser().getName());
    }

    @EventListener
    public void handleSessionEnd(SessionDisconnectEvent disconnectEvent) {
        personService.endSession(disconnectEvent.getSessionId());
    }
}