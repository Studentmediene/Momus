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

package no.dusken.momus.mapper;

import no.dusken.momus.model.Group;
import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;


public class GroupMapperTest extends AbstractTestRunner {

    @Autowired
    GroupMapper groupMapper;

    @Test
    public void testGroupFromJson() {
        String json = "{\"id\": 1, \"name\": \"teknisk\"}";
        Group group = groupMapper.groupFromJson(json);

        assertEquals(1L, group.getId().longValue());
        assertEquals("teknisk", group.getName());
    }

    @Test
    public void testNoGroupForInvalidJSon() {
        String json = "{lol: 'haha'}";
        Group group = groupMapper.groupFromJson(json);

        assertTrue(group == null);
    }

    @Test
    public void testGroupsFromJson() {
        String json = "{\"total_pages\": 1, \"objects\": [{\"id\": 1, \"name\": \"teknisk\"}, {\"id\": 2, \"name\": \"admin\"}, {\"id\": 3, \"name\": \"marked\"}, {\"id\": 4, \"name\": \"Flerkamera\"}, {\"id\": 5, \"name\": \"Humor\"}, {\"id\": 6, \"name\": \"Aktuelt\"}, {\"id\": 7, \"name\": \"Produksjon\"}, {\"id\": 8, \"name\": \"Serie\"}, {\"id\": 9, \"name\": \"Feature\"}, {\"id\": 10, \"name\": \"Funker\"}, {\"id\": 11, \"name\": \"Panger\"}], \"num_results\": 11, \"page\": 1}";
        List<Group> groups = groupMapper.groupsFromJson(json, "objects");

        assertEquals(11, groups.size());
        assertEquals("teknisk", groups.get(0).getName());
        assertEquals("admin", groups.get(1).getName());
        assertEquals("Panger", groups.get(10).getName());
    }

    @Test
    public void testGroupsFromJsonRoot() {
        String json = "[{\"id\": 5, \"name\": \"Humor\"}, {\"id\": 6, \"name\": \"Aktuelt\"}, {\"id\": 7, \"name\": \"Produksjon\"}, {\"id\": 8, \"name\": \"Serie\"}, {\"id\": 9, \"name\": \"Feature\"}, {\"id\": 10, \"name\": \"Funker\"}, {\"id\": 11, \"name\": \"Panger\"}]";
        List<Group> groups = groupMapper.groupsFromJson(json);

        assertEquals(7, groups.size());
        assertEquals("Humor", groups.get(0).getName());
    }

}
