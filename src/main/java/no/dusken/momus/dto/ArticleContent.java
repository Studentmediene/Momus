package no.dusken.momus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ArticleContent {
    @JsonProperty
    String getContent();
}