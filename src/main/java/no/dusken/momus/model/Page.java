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

import lombok.*;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "pageNr", callSuper = true)
@ToString(of = {"pageNr", "publication"}, callSuper = true)
@Builder(toBuilder = true)
public class Page extends AbstractEntity implements Comparable<Page>, Comparator<Page>{
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

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Advert> adverts;

    @ManyToOne(fetch = FetchType.EAGER)
    private LayoutStatus layoutStatus;

    @Override
    public int compareTo(Page page) {
        return pageNr - page.getPageNr();
    }

    @Override
    public int compare(Page page, Page t1) {
        return page.compareTo(t1);
    }
}
