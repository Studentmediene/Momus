package no.dusken.momus.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
// because otherwise it will default to class name ("Group"), which is a reserved mysql word
@Table(name = "group_table")
public class Group {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Group() {

    }

    public Group(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
