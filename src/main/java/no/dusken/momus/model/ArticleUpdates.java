package no.dusken.momus.model;

import java.util.Set;

public class ArticleUpdates {
    private Article article;
    private Set<String> updatedFields;


    public Article getArticle() {
        return article;
    }

    public Set<String> getUpdatedFields() {
        return updatedFields;
    }
}
