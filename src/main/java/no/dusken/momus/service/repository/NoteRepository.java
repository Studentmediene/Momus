package no.dusken.momus.service.repository;

import no.dusken.momus.model.Note;
import no.dusken.momus.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

    public Note findByOwner(Person owner);

    public Note findByOwner_Id(Long owner);

}
