package no.dusken.momus.controller;

import no.dusken.momus.model.Note;
import org.junit.Test;

import static no.dusken.momus.util.TestUtil.toJsonString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.is;

public class NoteControllerTest extends AbstractControllerTest {

    @Test
    public void getNotSetNote() throws Exception {
        performGetExpectOk("/note")
                .andExpect(jsonPath("$.content", is("Her kan du skrive personlige notater.")));
    }

    @Test
    public void setAndGetNote() throws Exception {
        Note note = new Note();
        String noteContent = "This is my note";
        note.setContent(noteContent);
        String noteString = toJsonString(note);

        performPutExpectOk("/note", noteString);

        performGetExpectOk("/note")
                .andExpect(jsonPath("$.owner.id", is(1)))
                .andExpect(jsonPath("$.content", is(noteContent)));
    }

    @Test
    public void editNote() throws Exception {
        Note note1 = new Note();
        note1.setContent("This is my note");
        String note1String = toJsonString(note1);

        Note note2 = new Note();
        note2.setContent("This is my second note");
        String note2String = toJsonString(note2);

        performPutExpectOk("/note", note1String);
        performGetExpectOk("/note")
                .andExpect(jsonPath("$.owner.id", is(1)))
                .andExpect(jsonPath("$.content", is(note1.getContent())));

        performPutExpectOk("/note", note2String);
        performGetExpectOk("/note")
                .andExpect(jsonPath("$.owner.id", is(1)))
                .andExpect(jsonPath("$.content", is(note2.getContent())));
    }

}
