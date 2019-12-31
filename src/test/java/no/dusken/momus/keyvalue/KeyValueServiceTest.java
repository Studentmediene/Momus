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

package no.dusken.momus.keyvalue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import no.dusken.momus.common.AbstractServiceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Optional;

import static org.mockito.AdditionalAnswers.*;

@Transactional
public class KeyValueServiceTest extends AbstractServiceTest {

    @InjectMocks
    private KeyValueService keyValueService;

    @Mock
    private KeyValueRepository keyValueRepository;

    private KeyValue keyValue1;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        keyValue1 = KeyValue.builder().key("DAKEY").value("2342").build();
    }

    @Test
    public void testSetValueString() {
        when(keyValueRepository.save(any(KeyValue.class))).then(returnsFirstArg());

        keyValueService.setValue("Testing", "123");

        verify(keyValueRepository, times(1)).save(any(KeyValue.class));
    }

    @Test
    public void testSetValueLong() {
        KeyValueService keyValueServiceSpy = spy(keyValueService);

        doNothing().when(keyValueServiceSpy).setValue(anyString(), anyString());

        keyValueServiceSpy.setValue("Testing", 123);

        verify(keyValueServiceSpy, times(1)).setValue("Testing", "123");
    }

    @Test
    public void testGetValue() {
        when(keyValueRepository.findById(anyString())).thenReturn(Optional.empty());
        when(keyValueRepository.findById(keyValue1.getKey())).thenReturn(Optional.of(keyValue1));

        String value;

        //Existing no default
        value = keyValueService.getValue(keyValue1.getKey());
        assertEquals(value, keyValue1.getValue());

        //Non existing no default
        value = keyValueService.getValue("nonexisting");
        assertTrue(value == null);

        //Existing default
        value = keyValueService.getValue(keyValue1.getKey(), "Defalut");
        assertEquals(value, keyValue1.getValue());

        //Non existing default
        value = keyValueService.getValue("nonexisting", "Defalut");
        assertEquals(value, "Defalut");
    }

    @Test
    public void testGetValueAsLong() {
        when(keyValueRepository.findById(anyString())).thenReturn(Optional.empty());
        when(keyValueRepository.findById(keyValue1.getKey())).thenReturn(Optional.of(keyValue1));

        long value;

        //Existing no default
        value = keyValueService.getValueAsLong(keyValue1.getKey());
        assertEquals(value, Long.parseLong(keyValue1.getValue()));

        //Non existing no default
        value = keyValueService.getValueAsLong("nonexisting");
        assertTrue(value == 0L);

        //Existing default
        value = keyValueService.getValueAsLong(keyValue1.getKey(), 23L);
        assertEquals(value, Long.parseLong(keyValue1.getValue()));

        //Non existing default
        value = keyValueService.getValueAsLong("nonexisting", 23);
        assertEquals(value, 23);

    }
}
