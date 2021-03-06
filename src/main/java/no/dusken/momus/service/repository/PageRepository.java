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

import no.dusken.momus.dto.PageId;
import no.dusken.momus.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

    List<Page> findByPublicationId(Long id);

    @Query("select new no.dusken.momus.dto.PageId(page.id) from Page page where page.publication.id = :pubId order by page.pageNr asc")
    List<PageId> getPageOrderByPublicationId(@Param("pubId") Long pubId);

    List<Page> findByPublicationIdOrderByPageNrAsc(Long id);

    @Modifying
    @Query("update Page p set p.pageNr = :pageNr where p.id = :pageId")
    void updatePageNr(@Param("pageNr") Integer pageNr, @Param("pageId") Long pageId);

    void deleteByPublicationId(Long id);

    int countByLayoutStatusIdAndPublicationId(Long status, Long id);
}
