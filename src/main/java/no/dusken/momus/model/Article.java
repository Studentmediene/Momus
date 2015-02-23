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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String content;

    private String note;

    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    private Section section;

    @ManyToOne(fetch = FetchType.EAGER)
    private ArticleStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    private ArticleType type;

    @ManyToOne(fetch = FetchType.EAGER)
    private Publication publication;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "article_journalist")
    @Fetch(FetchMode.SUBSELECT)
    private Set<Person> journalists;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "article_photographer")
    @Fetch(FetchMode.SUBSELECT)
    private Set<Person> photographers;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    private String photoStatus;

    private String googleDriveId;

    private String rawcontent;

    private int contentLength;

    /** If the article is assigned illustrator(s), not photographer(s) */
    private boolean useIllustration;


    public Article() {

    }

    public Article(Long id) {
        this.id = id;
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

    public String getPhotoStatus() {
        return photoStatus;
    }

    public void setPhotoStatus(String photoStatus) {
        this.photoStatus = photoStatus;
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

    public Publication getPublication() {return publication; }

    public void setPublication(Publication publication) {this.publication = publication; }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public String getGoogleDriveId() {
        return googleDriveId;
    }

    public void setGoogleDriveId(String googleDriveId) {
        this.googleDriveId = googleDriveId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;

        if (!id.equals(article.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getRawcontent() { return rawcontent; }

    public void setRawcontent(String rawContent) { this.rawcontent = rawContent; }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public boolean getUseIllustration() {
        return useIllustration;
    }

    public void setUseIllustration(boolean useIllustration) {
        this.useIllustration = useIllustration;
    }
}
