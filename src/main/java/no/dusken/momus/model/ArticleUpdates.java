package no.dusken.momus.model;

import java.util.Set;

public class ArticleUpdates extends Article {
    private Set<String> updatedFields;

    public Set<String> getUpdatedFields() {
        return updatedFields;
    }
}
