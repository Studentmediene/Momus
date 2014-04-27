package no.dusken.momus.model;

import javax.persistence.*;
import java.security.interfaces.DSAKey;
import java.util.Set;

@Entity
public class Disposition {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    private Publication publication;

    @OneToMany
    private Set<Page> pages;

    public Disposition(){}

    public Disposition(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Set<Page> getPages() {
        return pages;
    }

    public void setPages(Set<Page> pages) {
        this.pages = pages;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public Publication getPublication() {
        return publication;
    }
}
