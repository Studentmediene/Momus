package no.dusken.momus.model.websocket;

import java.util.Date;
import no.dusken.momus.model.Person;

public class Message {
    private Person from;
    private String text;
    private String action;
    private Date date;

    public Person getFrom(){
        return this.from;
    }

    public String getText(){
        return this.text;
    }

	public String getAction() {
		return this.action;
    }
    
    public void setFrom(Person from){
        this.from = from;
    }

    public void setText(String text){
        this.text = text;
    }

	public void setAction(String action) {
		this.action = action;
	}

	public Date getDate() {
		return date;
	}
}