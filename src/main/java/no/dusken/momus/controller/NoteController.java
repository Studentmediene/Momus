package no.dusken.momus.controller;

import no.dusken.momus.model.Note;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.NoteRepository;
import no.dusken.momus.service.PersonRepository;
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

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Note getNoteForLoggedInUser() {
        return noteRepository.findByOwner_Id(1L);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public @ResponseBody Note saveNoteForLoggedInUser(@RequestBody Note note) {
        Note updatedNote = noteRepository.findByOwner_Id(1L);

        if (updatedNote == null) { // no existing note for user
            updatedNote = new Note();
            note.setOwner(new Person(1L));
        }

        updatedNote.setContent(note.getContent());
        updatedNote = noteRepository.saveAndFlush(updatedNote);

        return updatedNote;
    }

}
