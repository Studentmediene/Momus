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

import com.google.api.client.util.IOUtils;
import no.dusken.momus.authentication.UserDetailsService;
import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.Avatar;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.PersonService;
import no.dusken.momus.service.repository.AvatarRepository;
import no.dusken.momus.service.repository.PersonRepository;
import no.dusken.momus.service.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@RestController
@Transactional
@RequestMapping("/api/person")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private PersonService personService;

    /**
     * Gets all active persons. In addition, if article ids are supplied, will return all contributors on those
     * even if they are inactive
     */
    @GetMapping
    public @ResponseBody Set<Person> getActivePersons(@RequestParam(
            value="articleIds", required = false, defaultValue = "") List<Long> articleIds) {
        return personService.getActivePersonsAndArticleContributors(articleIds);
    }

    @GetMapping("/{id}")
    public @ResponseBody Person getPersonById(@PathVariable("id") Long id) {
        return personRepository.findOne(id);
    }

    @GetMapping("/{id}/photo")
    public void getPersonPhoto(@PathVariable("id") Long id, HttpServletResponse response) throws IOException, SQLException {
        Avatar avatar = avatarRepository.findOne(id);

        if(avatar.getAvatar() == null) {
            throw new RestException("No photo found for user", 404);
        }

        response.addHeader("Content-Type", "image/jpeg");

        ServletOutputStream outStream = response.getOutputStream();
        IOUtils.copy(avatar.getAvatar().getBinaryStream(), outStream);
        outStream.flush();
        outStream.close();
    }

    @GetMapping("/me")
    public @ResponseBody Person getCurrentUser() {
        return userDetailsService.getLoggedInPerson();
    }

    @GetMapping("/me/photo")
    public void getCurrentUserPhoto(HttpServletResponse response) throws IOException, SQLException {
        getPersonPhoto(userDetailsService.getLoggedInPerson().getId(), response);
    }

    @PatchMapping("/me/favouritesection")
    public @ResponseBody Person updateFavouritesection(@RequestParam Long section) {
        if(!sectionRepository.exists(section)) {
            throw new RestException(
                    "Can't set favourite section, section with id " + section + " does not exist",
                    HttpServletResponse.SC_NOT_FOUND);
        }
        return personService.updateFavouritesection(
                userDetailsService.getLoggedInPerson(),
                sectionRepository.findOne(section));
    }

    @GetMapping("/loggedin/all")
    public @ResponseBody Set<Person> getAllActivePersons() {
        return personService.getAllLoggedInPersons();
    }

    @GetMapping("/loggedin")
    public @ResponseBody Set<Person> getActivePersonsAtState(@RequestParam String state) {
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