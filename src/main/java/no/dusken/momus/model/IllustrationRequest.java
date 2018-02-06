package no.dusken.momus.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class IllustrationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    private Article article;

    private int numberOfIllustrations;

    private int numberOfPages;

    private boolean isExternal;

    @ManyToOne(fetch = FetchType.EAGER)
    private Person requester;

    private String contactEmail;

    private LocalDate dueDate;

    private LocalDateTime created;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING, ACCEPTED, DENIED, COMPLETED
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfIllustrations() {
        return numberOfIllustrations;
    }

    public void setNumberOfIllustrations(int numberOfIllustration) {
        this.numberOfIllustrations = numberOfIllustration;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }

    public Person getRequester() {
        return requester;
    }

    public void setRequester(Person requester) {
        this.requester = requester;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
}
