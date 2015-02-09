package no.dusken.momus.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Section(){}

    public Section(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

}
