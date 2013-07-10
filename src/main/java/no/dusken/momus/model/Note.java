package no.dusken.momus.model;

import javax.persistence.*;

@Entity
public class Note {

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    @Column(length = 40960)
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    private Person owner;


    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }
}
