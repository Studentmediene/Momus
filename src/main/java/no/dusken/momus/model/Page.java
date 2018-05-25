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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@ToString(of = {"pageNr"}, callSuper = true)
@Builder(toBuilder = true)
public class Page extends AbstractEntity implements Comparable<Page>, Comparator<Page>, Messageable {
    @JsonIgnore private int pageNr;
    private String note;
    private boolean advertisement;
    private boolean web;
    private boolean done;

    @ManyToOne
    @JoinColumn(nullable = false, updatable = false) // Should not be able to change the publication of a page
    private Publication publication;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    private Set<Article> articles;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    private Set<Advert> adverts;

    @ManyToOne
    private LayoutStatus layoutStatus;

    @Override
    public int compareTo(Page page) {
        return pageNr - page.getPageNr();
    }

    @Override
    public int compare(Page page, Page t1) {
        return page.compareTo(t1);
    }

    @Override
    @JsonIgnore
    public List<String> getDestinations() {
        return Collections.singletonList(
                "/ws/publications/" + publication.getId() + "/pages"
        );
    }
}
