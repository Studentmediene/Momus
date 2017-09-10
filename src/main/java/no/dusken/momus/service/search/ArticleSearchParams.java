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

package no.dusken.momus.service.search;


import java.util.List;

public class ArticleSearchParams {
    private String free;
    private Long status;
    private List<Long> persons;
    private Long review;
    private Long section;
    private Long publication;

    private int pageSize = 200;
    private int pageNumber = 1;
    private boolean archived = false;


    public ArticleSearchParams() {
        // empty
    }

    public ArticleSearchParams(String free, Long status, List<Long> persons, Long review, Long section, Long publication, int pageSize, int pageNumber, boolean archived) {
        this.free = free;
        this.status = status;
        this.persons = persons;
        this.review = review;
        this.section = section;
        this.publication = publication;

        this.pageSize = pageSize;
        this.pageNumber = pageNumber;

        this.archived = archived;
    }

    /**
     * Jackson, the JSON converter will use this one, so we can limit the size
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 500 ? pageSize : 500;
    }

    public Long getPublication() {
        return publication;
    }

    public List<Long> getPersons() {
        return persons;
    }

    public String getFree() {
        return free;
    }

    public Long getStatus() {
        return status;
    }

    public Long getReview() {return review;}

    public Long getSection() {
        return section;
    }

    public boolean getArchived() { return archived; }

    @Override
    public String toString() {
        return "ArticleSearchParams{" +
                "free='" + free + '\'' +
                ", status='" + status + '\'' +
                ", review='" + review + '\'' +
                ", persons=" + persons +
                ", section='" + section + '\'' +
                ", publication='" + publication + '\'' +
                ", pageSize='" + this.pageSize + '\'' +
                ", pageNumber='" + this.pageNumber + '\'' +
                ", archived='" + archived + '\''+
                '}';
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getStartOfPage() {
        return pageSize * (pageNumber - 1);
    }

}
