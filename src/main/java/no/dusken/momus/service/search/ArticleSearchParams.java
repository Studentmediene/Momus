package no.dusken.momus.service.search;


import java.util.List;


public class ArticleSearchParams {

    private String free;
    private Long status;
    private List<Long> persons;
    private Long section;
    private Long publication;

    public ArticleSearchParams() {
        // empty
    }

    public ArticleSearchParams(String free, Long status, List<Long> persons, Long section, Long publication) {
        this.free = free;
        this.status = status;
        this.persons = persons;
        this.section = section;
        this.publication = publication;
    }

    public Long getPublication() {
        return publication;
    }

    public List<Long> getPersons() {return persons; }

    public String getFree() {
        return free;
    }

    public Long getStatus() {
        return status;
    }


    public Long getSection() {
        return section;
    }

    @Override
    public String toString() {
        return "ArticleSearchParams{" +
                "free='" + free + '\'' +
                ", status='" + status + '\'' +
                ", persons=" + persons +
                ", section='" + section + '\'' +
                ", publication='" + publication + '\'' +
                '}';
    }
}
