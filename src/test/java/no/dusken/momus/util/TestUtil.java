package no.dusken.momus.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.dusken.momus.mapper.HibernateAwareObjectMapper;

public class TestUtil {


    // Method taken from https://github.com/matsev/spring-testing/blob/master/src/test/java/com/jayway/application/BankApplicationTest.java
    public static String toJsonString(Object object) {
        // Use "our" ObjectMapper, it converts property names
        ObjectMapper objectMapper = new HibernateAwareObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
