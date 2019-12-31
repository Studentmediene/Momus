package no.dusken.momus.article;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ArticleContent {
    @JsonProperty
    String getContent();
}