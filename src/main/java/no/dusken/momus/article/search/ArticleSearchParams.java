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

package no.dusken.momus.article.search;


import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.dusken.momus.article.ArticleReviewStatus;
import no.dusken.momus.article.ArticleStatus;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ArticleSearchParams {
    private String free;
    private ArticleStatus status;
    private ArticleReviewStatus review;
    private List<Long> persons;
    private Long section;
    private Long publication;

    private int pageSize = 200;
    private int pageNumber = 1;
    private boolean archived = false;

    /**
     * Jackson, the JSON converter will use this one, so we can limit the size
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 500 ? pageSize : 500;
    }

    @JsonIgnore
    public int getStartOfPage() {
        return pageSize * pageNumber;
    }

}
