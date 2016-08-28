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

package no.dusken.momus.service;

import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@Transactional
public class KeyValueServiceTest extends AbstractTestRunner {

    @Autowired
    KeyValueService keyValueService;

    @Test
    public void canSetAndRetrieveValue() {
        keyValueService.setValue("My key", "This is my value");

        String value = keyValueService.getValue("My key");

        assertEquals("This is my value", value);
    }

    @Test
    public void returnsLastSetValue() {
        keyValueService.setValue("My key", "Value1");
        keyValueService.setValue("My key", "Value2");
        keyValueService.setValue("My key", "Value3");

        String value = keyValueService.getValue("My key");
        assertEquals("Value3", value);
    }

    @Test
    public void returnsDefaultValueIfNotSet() {
        String value = keyValueService.getValue("My key", "Default");

        assertEquals("Default", value);
    }

    @Test
    public void doesNotReturnDefaultValueIfSet() {
        keyValueService.setValue("My key", "Super good value");
        String value = keyValueService.getValue("My key", "Default");

        assertEquals("Super good value", value);
    }

    @Test
    public void zeroWhenLongNotSet() {
        long value = keyValueService.getValueAsLong("My long");

        assertEquals(0L, value);
    }

    @Test
    public void canSetAndRetrieveAsLong() {
        keyValueService.setValue("My long", 5L);
        long value = keyValueService.getValueAsLong("My long");

        assertEquals(5L, value);
    }

    @Test
    public void canSetDefaultValueForLong() {
        long value = keyValueService.getValueAsLong("My long", 10L);

        assertEquals(10L, value);
    }
}
