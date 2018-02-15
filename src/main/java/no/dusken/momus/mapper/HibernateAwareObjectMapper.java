/*
 * Copyright 2016 Studentmediene i Trondheim AS
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * An ObjectMapper that registers a HibernateModule, so a JSON serialization
 * doesn't fail with Hibernate lazy-loading.
 */
public class HibernateAwareObjectMapper extends ObjectMapper {

    static final long serialVersionUID = 23L;

    public HibernateAwareObjectMapper() {
        // converts lastName to last_name and the other way
        setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        registerModule(new Hibernate5Module());

        // frontend expects times/dates as milliseconds since epoch
        registerModule(new JavaTimeModule());
        disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
    }
}
