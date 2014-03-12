package no.dusken.momus.model;


import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.util.List;
import java.util.Set;


public class Search {

    private String free;
    private String status;
    private Set<Person> persons;
//    private Set<Person> journalists;
    private String section;
    private String publication;

    public String getPublication() {
        return publication;
    }

    public Set<Person> getPersons() {return persons; }

    public String getFree() {
        return free;
    }

    public String getStatus() {
        return status;
    }


    public String getSection() {
        return section;
    }

}
