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
import java.util.List;

@Entity
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int pageNr;
    private String note;
    private boolean advertisement;

    @ManyToMany(fetch = FetchType.EAGER)
    @OrderColumn
    private List<Article> articles;

    @ManyToOne
    private Section section;



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

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }
}
