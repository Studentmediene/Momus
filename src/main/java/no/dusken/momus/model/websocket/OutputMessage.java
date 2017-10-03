package no.dusken.momus.model.websocket;

import java.util.Date;
import no.dusken.momus.model.Person;

public class OutputMessage {
    private Person from;
    private String text;
    private Date date;
    private String action;
    
    public OutputMessage(Person from, String text, Date date, String action) {
        this.from = from;
        this.text = text;
        this.date = date;
        this.action = action;
    }

    public Person getFrom() {
        return this.from;
    }

	public String getText() {
		return this.text;
	}

	public Date getDate() {
		return this.date;
	}

	public String getAction() {
		return this.action;
	}
}