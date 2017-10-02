/*
 * Copyright 2016 Studentmediene i Trondheim AS
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
import java.util.Comparator;
import java.util.Set;

public class Page implements Comparable<Page>, Comparator<Page>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int pageNr;
    private String note;
    private boolean advertisement;
    private boolean web;
    private boolean done;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, updatable = false) // Should not be able to change the publication of a page
    private Publication publication;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Article> articles;

    @ManyToOne(fetch = FetchType.EAGER)
    private LayoutStatus layoutStatus;

    public Page(){

    }

    public Page(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public int getPageNr() {
        return pageNr;
    }

    public void setPageNr(int pageNr) {
        this.pageNr = pageNr;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setAdvertisement(boolean advertisement){
        this.advertisement = advertisement;
    }
    public boolean isAdvertisement() {
        return this.advertisement;
    }

    public void setWeb(boolean web) {this.web = web;}
    public boolean isWeb() { return this.web; }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Set<Article> getArticles() {
        return articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public LayoutStatus getLayoutStatus() {
        return layoutStatus;
    }

    public void setLayoutStatus(LayoutStatus layoutStatus) {
        this.layoutStatus = layoutStatus;
    }

    @Override
    public String toString() {
        return publication.getId() + " page: " + pageNr;
    }

    @Override
    public int compareTo(Page page) {
        return pageNr - page.getPageNr();
    }

    @Override
    public int compare(Page page, Page t1) {
        return page.compareTo(t1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        if (!id.equals(page.id)) return false;

        return true;
    }
}
