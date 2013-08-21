package no.dusken.momus.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Article {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String content;
    private String note;

    @Transient
    private int contentLength;

    @OneToOne(fetch = FetchType.EAGER)
    private ArticleStatus status;
    @OneToOne(fetch = FetchType.EAGER)
    private ArticleType type;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "article_journalist")
    private Set<Person> journalists;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "article_photographer")
    private Set<Person> photographers;

    @ManyToOne
    private Person correctResponsible;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    /**
     * This is done after load instead of in a getter, since
     * it may happen that the content is set to empty to save bandwith/hide it, but
     * the length of it is still needed.
     */
    @PostLoad
    private void calculateContentLength() {
        contentLength = content.length();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getContentLength() {
        return contentLength;
    }

    public ArticleStatus getStatus() {
        return status;
    }

    public void setStatus(ArticleStatus status) {
        this.status = status;
    }

    public ArticleType getType() {
        return type;
    }

    public void setType(ArticleType type) {
        this.type = type;
    }

    public Set<Person> getJournalists() {
        return journalists;
    }

    public void setJournalists(Set<Person> journalists) {
        this.journalists = journalists;
    }

    public Set<Person> getPhotographers() {
        return photographers;
    }

    public void setPhotographers(Set<Person> photographers) {
        this.photographers = photographers;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Person getCorrectResponsible() {
        return correctResponsible;
    }

    public void setCorrectResponsible(Person correctResponsible) {
        this.correctResponsible = correctResponsible;
    }
}
