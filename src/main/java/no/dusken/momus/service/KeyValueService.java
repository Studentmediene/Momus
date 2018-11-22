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

package no.dusken.momus.service;

import lombok.extern.slf4j.Slf4j;
import no.dusken.momus.model.KeyValue;
import no.dusken.momus.service.repository.KeyValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KeyValueService {

    @Autowired
    KeyValueRepository keyValueRepository;

    /**
     * Returns the value belonging to the key, or null if key not found
     */
    public String getValue(String key) {
        KeyValue keyValue = keyValueRepository.getOne(key);
        return keyValue != null ? keyValue.getValue() : null;
    }

    /**
     * Returns the value belonging to the key, or the provided
     * default value if key not found
     */
    public String getValue(String key, String defaultValue) {
        String value = getValue(key);
        if (value == null) {
            value = defaultValue;
        }

        log.debug("Value for {} was {}", key, value);
        return value;
    }

    /**
     * Returns the value belonging to the key, or
     * the long 0L if not set
     */
    public long getValueAsLong(String key) {
        return getValueAsLong(key, 0L);
    }

    /**
     * Returns the value belonging to the key, or the provided
     * default value if key not found
     */
    public long getValueAsLong(String key, long defaultValue) {
        String stringValue = getValue(key);
        if (stringValue == null) {
            return defaultValue;
        }
        return Long.parseLong(stringValue);
    }

    public void setValue(String key, String value) {
        KeyValue pair = KeyValue.builder().key(key).value(value).build();
        keyValueRepository.save(pair);

        log.info("Key {} was assigned value {}", key, value);
    }

    public void setValue(String key, long value) {
        setValue(key, String.valueOf(value));
    }
}
