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
import java.time.ZonedDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@ToString(of = {"article", "savedDate"}, callSuper = true)
@Builder(toBuilder = true)
public class ArticleRevision extends AbstractEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    private String content;

    @ManyToOne
    private ArticleStatus status;

    private ZonedDateTime savedDate;

    private boolean statusChanged;
}
