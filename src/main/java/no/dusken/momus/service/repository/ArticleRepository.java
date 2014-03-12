/*
 * Copyright 2013 Studentmediene i Trondheim AS
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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

import java.util.List;
import java.util.Set;

public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {

    public List<Article> findByNameOrStatus_NameOrJournalistsOrPhotographersOrPublication_Name(String articleName, String status, Set<Person> journalists, Set<Person> photographers, String publication);
    public List<Article> findByStatus_Name(String status);
    public List<Article> findByJournalistsOrPhotographers(Set<Person> journalists, Set<Person> photographers);
    public List<Article> findByPublication_Name(String publicationName);

}
