package no.dusken.momus.dto;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.dusken.momus.model.Article;
import no.dusken.momus.model.Page;

public interface FullPublication extends SimplePublication {
    @JsonProperty
    @JsonIgnoreProperties(value = "publication")
    Set<Article> getArticles();

    @JsonProperty
    @JsonIgnoreProperties(value = "publication")
    List<Page> getPages();
}