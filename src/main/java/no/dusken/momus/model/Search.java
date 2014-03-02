package no.dusken.momus.model;


import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
public class Search {

    @javax.persistence.Id
    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String status;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Publication> publications;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Person> persons;

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<Publication> getPublications() {
        return publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }

}
