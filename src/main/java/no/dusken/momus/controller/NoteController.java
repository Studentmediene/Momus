package no.dusken.momus.controller;

import no.dusken.momus.authentication.UserLoginService;
import no.dusken.momus.model.Note;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.NoteRepository;
import no.dusken.momus.service.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/note")
public class NoteController {

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    UserLoginService userLoginService;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Note getNoteForLoggedInUser() {
        Note note = noteRepository.findByOwner_Id(userLoginService.getId());

        if (note == null) {
            note = new Note();
            note.setOwner(new Person(userLoginService.getId()));
            note = noteRepository.saveAndFlush(note);
        }

        return note;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public @ResponseBody Note saveNoteForLoggedInUser(@RequestBody Note note) {
        Long userId = userLoginService.getId();

        Note updatedNote = noteRepository.findByOwner_Id(userId);

        updatedNote.setContent(note.getContent());
        updatedNote = noteRepository.saveAndFlush(updatedNote);

        return updatedNote;
    }

}
