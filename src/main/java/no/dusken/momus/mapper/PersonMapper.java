/*
 * Copyright 2014 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.dusken.momus.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import no.dusken.momus.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PersonMapper {

    Logger logger = LoggerFactory.getLogger(getClass());
    
    private ObjectMapper mapper;

    public PersonMapper() {
        mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Person personFromJson(String json) {
        Person person = null;
        try {
            person = mapper.readValue(json, Person.class);
        } catch (IOException e) {
            logger.warn("Couldn't create single person from json:", e);
        }
        return person;
    }

    /**
     * Converts JSON to a list of persons. Root element specify if the persons are not the
     * top element of the json. For instance for this JSON one should specify that the persons
     * are inside "personObjects":
     * {
     *  "someValue": 33,
     *  "personObjects": [list of persons]
     * }
     */
    public List<Person> personsFromJson(String json, String rootElement) {
        List<Person> persons = null;
        try {

            JsonParser parser = mapper.getFactory().createParser(json);
            
            if (rootElement != null) { // go inside the specified root element
                JsonNode root = mapper.readTree(parser);
                parser = root.path(rootElement).traverse();
            }

            persons = mapper.readValue(parser, new TypeReference<List<Person>>() {});
        } catch (IOException e) {
            logger.warn("Couldn't create persons from json:", e);
        }

        return persons;
    }

    public List<Person> personsFromJson(String json) {
        return personsFromJson(json, null);
    }
}
