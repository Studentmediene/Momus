package no.dusken.momus.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Article {
    // TypedQuery<UserAccount> q = em.createQuery("select u from UserAccount u where u.id in (:myList)", UserAccount.class);

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String content;

    @Transient
    private int contentLength;

    @OneToOne(fetch = FetchType.EAGER)
    private ArticleStatus status;
    @OneToOne(fetch = FetchType.EAGER)
    private ArticleType type;


    @ManyToMany(fetch = FetchType.EAGER)
    private List<Person> journalists;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Person> photographers;

    @ManyToOne
    private Person correctResponsible;

    @Temporal(TemporalType.DATE)
    private Date lastUpdated;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private Publication publication;
    // section?

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

    public List<Person> getJournalists() {
        return journalists;
    }

    public void setJournalists(List<Person> journalists) {
        this.journalists = journalists;
    }

    public List<Person> getPhotographers() {
        return photographers;
    }

    public void setPhotographers(List<Person> photographers) {
        this.photographers = photographers;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
