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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@ToString(of = {"name", "releaseDate"}, callSuper = true)
@Builder(toBuilder = true)
public class Publication extends AbstractEntity implements Messageable {
    private String name;

    private LocalDate releaseDate;

    @OneToMany(mappedBy = "publication", fetch = FetchType.LAZY)
    private Set<Article> articles;

    @OneToMany(mappedBy = "publication")
    private List<Page> pages;

    @Override
    @JsonIgnore
    public List<String> getDestinations() {
        return Arrays.asList(
                "/ws/publications",
                "/ws/publications/" + id
        );
    }
}
