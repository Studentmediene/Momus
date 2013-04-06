package no.dusken.momus.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ArticleType {

    @Id
    private Long id;

    private String name;
    private String description;


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
