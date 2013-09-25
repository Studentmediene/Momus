/*
 * Copyright 2013 Studentmediene i Trondheim AS
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

package no.dusken.momus.authentication;

import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
public class SmmAbTokenTest extends AbstractTestRunner {

    @Test
    public void testInitSmmDbToken1() {
        String json = "{\"username\": \"mats\", \"timestamp\": \"2013-09-21T23:21:50.232335\", \"userid\": 594, \"sign\": \"6b739b888a56c0122df93d73bb63e02ac70664d7\"}";

        SmmAbToken token = new SmmAbToken(json);

        assertEquals("mats", token.getUsername());
        assertEquals(594L, token.getId().longValue());
        assertEquals(json, token.getJsonText());
    }

    @Test
    public void testInitSmmDbToken2() {
        String json = "{\"username\": \"æææøøøøååå\", \"timestamp\": \"2013-09-21T23:21:50.232335\", \"userid\": 11, \"sign\": \"6b739b888a56c0122df93d73bb63e02ac70664d7\"}";

        SmmAbToken token = new SmmAbToken(json);

        assertEquals("æææøøøøååå", token.getUsername());
        assertEquals(11L, token.getId().longValue());
        assertEquals(json, token.getJsonText());
    }

    @Test(expected = RestException.class)
    public void testInvalidSmmDbToken1() {
        String json = "{}";

        SmmAbToken token = new SmmAbToken(json);
    }

    @Test(expected = RestException.class)
    public void testInvalidSmmDbToken2() {
        String json = "{\"name\": \"æææøøøøååå\", \"timestamp\": \"2013-09-21T23:21:50.232335\", \"userid\": 11, \"sign\": \"6b739b888a56c0122df93d73bb63e02ac70664d7\"}";

        SmmAbToken token = new SmmAbToken(json);
    }



}
