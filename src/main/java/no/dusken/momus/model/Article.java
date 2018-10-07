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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import no.dusken.momus.service.ArticleService;
import no.dusken.momus.service.remotedocument.RemoteDocumentService;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@ToString(of = {"name", "section", "type", "status"}, callSuper = true)
@Builder(toBuilder = true)
public class Article extends AbstractEntity implements Messageable {
    private String name;

    private String remoteId;

    private String remoteUrl;

    @Enumerated(EnumType.STRING)
    private RemoteDocumentService.ServiceName remoteServiceName;

    @JsonIgnore
    private String content;

    @JsonIgnore
    private String rawcontent;

    private int contentLength;

    private String imageText;

    private String note;

    private String comment;

    private ZonedDateTime lastUpdated;

    @ManyToOne
    private Section section;

    @ManyToOne
    private ArticleStatus status;

    @ManyToOne
    private ArticleType type;

    @ManyToOne
    private ArticleReview review;

    private boolean quoteCheckStatus;

    private String photoStatus;

    @ManyToOne
    @JsonIgnoreProperties(value = {"articles", "pages"})
    private Publication publication;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "article_journalist")
    @Fetch(FetchMode.SUBSELECT)
    private Set<Person> journalists;

    private String externalAuthor;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "article_photographer")
    @Fetch(FetchMode.SUBSELECT)
    private Set<Person> photographers;

    private String externalPhotographer;

    /** If the article is assigned illustrator(s), not photographer(s) */
    private boolean useIllustration;

    private boolean archived;

    public void setContent(String content) {
        this.content = content;
        String rawContent = ArticleService.createRawContent(this);
        this.setRawcontent(rawContent);
        this.setContentLength(rawContent.length());
    }

    @Override
    @JsonIgnore
    public List<String> getDestinations() {
        return Arrays.asList(
                "/ws/articles/",
                "/ws/articles/" + id,
                "/ws/publications/" + publication.getId() + "/articles"
        );
    }
}
