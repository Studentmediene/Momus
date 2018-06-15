package no.dusken.momus.model;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
public class NewsItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private ZonedDateTime date;

    @ManyToOne(fetch = FetchType.EAGER)
    private Person author;

    private String content;


    public NewsItem ( ) { }

    public NewsItem( Long id ) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate( ZonedDateTime date ) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent( String content ) {
        this.content = content;
    }
}
