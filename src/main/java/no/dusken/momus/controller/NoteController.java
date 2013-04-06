package no.dusken.momus.controller;

import no.dusken.momus.model.Note;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.NoteRepository;
import no.dusken.momus.service.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/note")
public class NoteController {

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    PersonRepository personRepository;

    @RequestMapping("/add")
    public @ResponseBody void lol() {
        Note note = new Note();
        note.setContent("note5for2");

//        Person person = personRepository.findOne(2L);
        Person person = new Person(2L);

        note.setOwner(person);

        noteRepository.save(note);
    }

    @RequestMapping("/get")
    public @ResponseBody List<Note> getAll() {
        return noteRepository.findAll();
    }

    @RequestMapping("/getByPerson")
    public @ResponseBody Note getByPerson() {
        return noteRepository.findByOwner_Id(1L);
    }
}
