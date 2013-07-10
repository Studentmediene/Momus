package no.dusken.momus.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ArticleStatus {

    @Id
    private Long id;

    private String name;
    private String color;


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
