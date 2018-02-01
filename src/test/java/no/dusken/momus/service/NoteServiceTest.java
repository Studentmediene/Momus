package no.dusken.momus.service;

import no.dusken.momus.model.Note;
import no.dusken.momus.service.repository.NoteRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.*;

@Transactional
public class NoteServiceTest extends AbstractServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    private Note note;

    @Before
    public void setUp() {
        userMockSetup();
        note = new Note(1L);
        note.setOwner(userDetailsService.getLoggedInPerson());
        note.setContent("Et notat");

        when(noteRepository.findByOwner_Id(userDetailsService.getLoggedInPerson().getId())).thenReturn(note);
        when(noteRepository.saveAndFlush(any(Note.class))).then(returnsFirstArg());
    }

    /**
     * Method: {@link NoteService#getNoteForLoggedInUser}
     */
    @Test
    public void testGetNoteForLoggedInUser() {
        Note note = noteService.getNoteForLoggedInUser();

        assertEquals(note.getOwner(), userDetailsService.getLoggedInPerson());
        assertEquals(note.getContent(), "Et notat");
    }

    /**
     * Method: {@link NoteService#saveNoteForLoggedInUser}
     */
    @Test
    public void testSaveNoteForLoggedInUser() {
        Note note = new Note();
        note.setContent("Nytt notat");
        note.setOwner(userDetailsService.getLoggedInPerson());

        note = noteService.saveNoteForLoggedInUser(note);

        verify(noteRepository, times(1)).saveAndFlush(note);

        assertEquals(note.getOwner(), userDetailsService.getLoggedInPerson());
        assertEquals(note.getContent(), "Nytt notat");
    }
}
