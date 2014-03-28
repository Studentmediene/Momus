/*
 * Copyright 2014 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    @Lob
    @Column(length = 40960)
    private String content;

    @Lob
    @Column(length = 40960)
    private String note;

    @Transient
    private int contentLength;

    @OneToOne(fetch = FetchType.EAGER)
    private ArticleStatus status;
    @OneToOne(fetch = FetchType.EAGER)
    private ArticleType type;


    @OneToOne(fetch = FetchType.EAGER)
    private Publication publication;

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

    public Publication getPublication() {return publication; }

    public void setPublication(Publication publication) {this.publication = publication; }
}
