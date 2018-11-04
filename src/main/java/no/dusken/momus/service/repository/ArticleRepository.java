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

package no.dusken.momus.service.repository;

import no.dusken.momus.model.Article;
import no.dusken.momus.model.Person;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByPublicationId(Long id);

    @Query("select a from Article a where :person member of a.journalists or :person member of a.photographers or :person member of a.graphics order by a.lastUpdated desc")
    List<Article> findByJournalistsOrPhotographersOrGraphicsContains(@Param("person") Person person, Pageable pageable);

    List<Article> findByGoogleDriveIdIn(Iterable<String> ids);

    int countByStatusIdAndPublicationId(Long articleStatus, Long id);

    int countByReviewIdAndPublicationId(Long articleStatus, Long id);
}
