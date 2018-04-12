package no.dusken.momus.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

@Entity
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    private Person author;

    private String content;


    public News ( ) { }

    public News( Long id ) { this.id = id; }

    public Long getId() { return id; }

    public Date getDate() { return date; }

    public void setDate( Date date ) { this.date = date; }

    public String getTitle() { return title; }

    public void setTitle( String title ) { this.title = title; }

    public Person getAuthor() { return author; }

    public void setAuthor(Person author) { this.author = author; }

    public String getContent() { return content; }

    public void setContent( String content ) { this.content = content; }
}
