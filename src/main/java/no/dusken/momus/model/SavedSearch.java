package no.dusken.momus.model;

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
    private String description;

    @Column(length = 40960)
    private String url;

    @OneToOne(fetch = FetchType.LAZY)
    private Person owner;

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public Person getOwner() { return owner; }

    public void setOwner(Person owner) { this.owner = owner; }
}
