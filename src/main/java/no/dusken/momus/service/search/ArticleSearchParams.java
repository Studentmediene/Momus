package no.dusken.momus.service.search;


import java.util.List;


public class ArticleSearchParams {
    private String free;
    private Long status;
    private List<Long> persons;
    private Long section;
    private Long publication;

    private int pageSize = 200;
    private int pageNumber = 1;
    private boolean archived = false;


    public ArticleSearchParams() {
        // empty
    }

    public ArticleSearchParams(String free, Long status, List<Long> persons, Long section, Long publication, int pageSize, int pageNumber, boolean archived) {
        this.free = free;
        this.status = status;
        this.persons = persons;
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


    public Long getSection() {
        return section;
    }

    public boolean getArchived() { return archived; }

    @Override
    public String toString() {
        return "ArticleSearchParams{" +
                "free='" + free + '\'' +
                ", status='" + status + '\'' +
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
