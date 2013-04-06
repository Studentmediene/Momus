package no.dusken.momus.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ArticleRevision {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Article article;

    private String content;

    @ManyToOne
    private ArticleStatus status;

    @ManyToOne
    private Person author;

    @Temporal(TemporalType.DATE)
    private Date savedDate;



    public Long getId() {
        return id;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArticleStatus getStatus() {
        return status;
    }

    public void setStatus(ArticleStatus status) {
        this.status = status;
    }

    public Date getSavedDate() {
        return savedDate;
    }

    public void setSavedDate(Date savedDate) {
        this.savedDate = savedDate;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }
}
