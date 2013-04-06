package no.dusken.momus.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

/**
 * An ObjectMapper that registers a HibernateModule, so a JSON serialization
 * doesn't fail with Hibernate lazy-loading.
 */
public class HibernateAwareObjectMapper extends ObjectMapper {

    public HibernateAwareObjectMapper() {
        registerModule(new Hibernate4Module());
    }
}
