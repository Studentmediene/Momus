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

package no.dusken.momus.service;

import no.dusken.momus.authentication.UserLoginService;
import no.dusken.momus.model.Note;
import no.dusken.momus.service.repository.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    private UserLoginService userLoginService;


    public Note getNoteForLoggedInUser() {
        Long userId = userLoginService.getId();
        Note note = noteRepository.findByOwner_Id(userId);
        if (note == null) {
            note = new Note();
            note.setContent("Her kan du skrive personlige notater.");
            logger.debug("No note found for user {}, returning a dummy", userId);
        }
        return note;
    }


    public Note saveNoteForLoggedInUser(Note note) {
        Long userId = userLoginService.getId();
        Note existing = noteRepository.findByOwner_Id(userId);
        if (existing == null) {
            existing = note;
            note.setOwner(userLoginService.getLoggedInUser());
            logger.info("Creating new note for userid {} with content: ", userId, note.getContent());
        } else {
            existing.setContent(note.getContent());
            logger.info("Updating note for userid {} with content: ", userId, note.getContent());
        }

        return noteRepository.saveAndFlush(existing);
    }

}
