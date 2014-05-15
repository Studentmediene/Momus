package no.dusken.momus.service.search;


import java.util.Set;


public class ArticleSearchParams {

    private String free;
    private String status;
    private Set<String> persons;
    private String section;
    private String publication;

    public ArticleSearchParams() {
        // empty
    }

    public ArticleSearchParams(String free, String status, Set<String> persons, String section, String publication) {
        this.free = free;
        this.status = status;
        this.persons = persons;
        this.section = section;
        this.publication = publication;
    }

    public String getPublication() {
        return publication;
    }

    public Set<String> getPersons() {return persons; }

    public String getFree() {
        return free;
    }

    public String getStatus() {
        return status;
    }


    public String getSection() {
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
