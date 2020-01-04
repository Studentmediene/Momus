package no.dusken.momus.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class HibernateAwareObjectMapper extends ObjectMapper {
    private static final long serialVersionUID = 1L;

    public HibernateAwareObjectMapper() {
        // converts lastName to last_name and the other way
        setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        registerModule(new Hibernate5Module());

        // frontend expects times/dates as milliseconds since epoch
        registerModule(new JavaTimeModule());
        disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
