package no.dusken.momus.model;

import no.dusken.momus.service.search.ArticleSearchParams;

import javax.persistence.*;

@Entity
public class SavedSearch {

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    @Column(length = 40960)
    private String name;

    @Column(length = 40960)
    private ArticleSearchParams url;

    @OneToOne(fetch = FetchType.LAZY)
    private Person owner;

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public ArticleSearchParams getUrl() { return url; }

    public void setUrl(ArticleSearchParams url) { this.url = url; }

    public Person getOwner() { return owner; }

    public void setOwner(Person owner) { this.owner = owner; }
}
