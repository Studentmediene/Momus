package no.dusken.momus.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface SimplePublication {
    @JsonProperty
    Long getId();

    @JsonProperty
    String getName();

    @JsonProperty
    LocalDate getReleaseDate();


}