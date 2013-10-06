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
import no.dusken.momus.model.Person;
import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class PersonMapperTest extends AbstractTestRunner {

    @Autowired
    PersonMapper personMapper;

    @Test
    public void testPersonFromJson() {
        String json = "{\"username\": \"mats\", \"phone_number\": \"47385324\", \"last_name\": \"Svensson\", \"last_updated\": \"2013-10-06T11:45:14.992792\", \"about\": \"Kul type!\", \"groups\": [{\"description\": \"Avis-sjefer\", \"id\": 14, \"name\": \"Redakt\\u00f8r\"}, {\"description\": \"De som fikser alt\", \"id\": 12, \"name\": \"IT\"}], \"active\": true, \"id\": 594, \"permissions\": [\"momus:editor\", \"smmdb:admin\", \"chimera:superuser\", \"momus:superuser\"], \"private_email\": \"mats@matsemann.com\", \"first_name\": \"Mats\", \"created\": \"2013-09-15T12:51:38.139661\", \"birthdate\": null, \"email\": \"mats.svensson@studentmediene.no\"}";
        Person person = personMapper.personFromJson(json);

        assertEquals("mats", person.getUsername());
        assertEquals("47385324", person.getPhoneNumber());
        assertEquals("Mats", person.getFirstName());
        assertEquals("Svensson", person.getLastName());
        assertEquals(true, person.isActive());

        Set<Group> groups = new HashSet<>();
        groups.add(new Group(14L, "Redaktør", "notneeded"));
        groups.add(new Group(12L, "IT", "notneeded"));
        assertEquals(groups, person.getGroups());

        Set<String> expectedPermissions = new HashSet<>();
        expectedPermissions.add("momus:editor");
        expectedPermissions.add("smmdb:admin");
        expectedPermissions.add("chimera:superuser");
        expectedPermissions.add("momus:superuser");

        assertEquals(expectedPermissions, person.getPermissions());

    }

    @Test
    public void testPersonFromEmptyJson() {
        String json = "";
        Person person = personMapper.personFromJson(json);
        assertTrue(person == null);
    }

    @Test
    public void testPersonFromInvalidJson() {
        String json = "{hei: hmmm";
        Person person = personMapper.personFromJson(json);
        assertTrue(person == null);
    }

    @Test
    public void testPersonsFromJson() throws Exception {
        String json = "{\"total_pages\": 1, \"objects\": [\n    {\n        \"username\": \"alebru\",\n        \"phone_number\": \"92233281\",\n        \"last_name\": \"Kj\\u00f8niksen Brucki\",\n        \"last_updated\": \"2013-09-04T18:35:18.664302\",\n        \"about\": \"Addresse:\\n\\n \\n\",\n        \"groups\": [\n            {\n                \"description\": \"STV Produksjon best\\u00e5r av eksternprodusenter og motiongrafikere. Sammen lager vi oppdragsfilm for eksterne akt\\u00f8rer. Vi har erfaring med produksjon av reklamefilm, film av arrangement og musikkvideo for \\u00e5 nevne noen.\",\n                \"id\": 7,\n                \"name\": \"Produksjon\"\n            }\n        ],\n        \"active\": true,\n        \"id\": 6,\n        \"permissions\": [],\n        \"private_email\": \"\",\n        \"first_name\": \"Alexander\",\n        \"created\": \"2013-09-04T18:35:18.664292\",\n        \"birthdate\": null,\n        \"email\": \"alexander.kj\\u00f8niksen brucki@studentmediene.no\"\n    },\n    {\n        \"username\": \"amahau\",\n        \"phone_number\": \"95175034\",\n        \"last_name\": \"Haug\",\n        \"last_updated\": \"2013-09-04T18:35:18.664884\",\n        \"about\": \"Addresse:\\n\\n \\n\",\n        \"groups\": [\n            {\n                \"description\": \"\",\n                \"id\": 8,\n                \"name\": \"Serie\"\n            }\n        ],\n        \"active\": true,\n        \"id\": 9,\n        \"permissions\": [\"momus:editor\", \"smmdb:admin\"],\n        \"private_email\": \"amanda_j_haug@hotmail.com\",\n        \"first_name\": \"Amanda J\\u00f8rgine\",\n        \"created\": \"2013-09-04T18:35:18.664875\",\n        \"birthdate\": null,\n        \"email\": \"amanda j\\u00f8rgine.haug@studentmediene.no\"\n    },\n    {\n        \"username\": \"andhod\",\n        \"phone_number\": \"46416928\",\n        \"last_name\": \"Hodneland\",\n        \"last_updated\": \"2013-09-04T18:35:18.666294\",\n        \"about\": \"Addresse:\\n\\n \\n\",\n        \"groups\": [\n            {\n                \"description\": \"\",\n                \"id\": 4,\n                \"name\": \"Flerkamera\"\n            }\n        ],\n        \"active\": true,\n        \"id\": 15,\n        \"permissions\": [],\n        \"private_email\": \"andreashodneland@gmail.com\",\n        \"first_name\": \"Andreas\",\n        \"created\": \"2013-09-04T18:35:18.666272\",\n        \"birthdate\": null,\n        \"email\": \"andreas.hodneland@studentmediene.no\"\n    }\n], \"num_results\": 3, \"page\": 1}";

        List<Person> persons = personMapper.personsFromJson(json, "objects");

        assertEquals(3, persons.size());
        assertEquals("alebru", persons.get(0).getUsername());
        assertEquals("Kjøniksen Brucki", persons.get(0).getLastName());
        assertEquals(1, persons.get(0).getGroups().size());
        assertTrue(persons.get(0).getGroups().contains(new Group(7L, "Produksjon", "")));

        assertEquals(2, persons.get(1).getPermissions().size());
    }

    @Test
    public void testPersonsFromJsonAtRoot() throws Exception {
        String json = "[\n    {\n        \"username\": \"alebru\",\n        \"phone_number\": \"92233281\",\n        \"last_name\": \"Kj\\u00f8niksen Brucki\",\n        \"last_updated\": \"2013-09-04T18:35:18.664302\",\n        \"about\": \"Addresse:\\n\\n \\n\",\n        \"groups\": [\n            {\n                \"description\": \"STV Produksjon best\\u00e5r av eksternprodusenter og motiongrafikere. Sammen lager vi oppdragsfilm for eksterne akt\\u00f8rer. Vi har erfaring med produksjon av reklamefilm, film av arrangement og musikkvideo for \\u00e5 nevne noen.\",\n                \"id\": 7,\n                \"name\": \"Produksjon\"\n            }\n        ],\n        \"active\": true,\n        \"id\": 6,\n        \"permissions\": [],\n        \"private_email\": \"\",\n        \"first_name\": \"Alexander\",\n        \"created\": \"2013-09-04T18:35:18.664292\",\n        \"birthdate\": null,\n        \"email\": \"alexander.kj\\u00f8niksen brucki@studentmediene.no\"\n    },\n    {\n        \"username\": \"amahau\",\n        \"phone_number\": \"95175034\",\n        \"last_name\": \"Haug\",\n        \"last_updated\": \"2013-09-04T18:35:18.664884\",\n        \"about\": \"Addresse:\\n\\n \\n\",\n        \"groups\": [\n            {\n                \"description\": \"\",\n                \"id\": 8,\n                \"name\": \"Serie\"\n            }\n        ],\n        \"active\": true,\n        \"id\": 9,\n        \"permissions\": [\"momus:editor\", \"smmdb:admin\"],\n        \"private_email\": \"amanda_j_haug@hotmail.com\",\n        \"first_name\": \"Amanda J\\u00f8rgine\",\n        \"created\": \"2013-09-04T18:35:18.664875\",\n        \"birthdate\": null,\n        \"email\": \"amanda j\\u00f8rgine.haug@studentmediene.no\"\n    },\n    {\n        \"username\": \"andhod\",\n        \"phone_number\": \"46416928\",\n        \"last_name\": \"Hodneland\",\n        \"last_updated\": \"2013-09-04T18:35:18.666294\",\n        \"about\": \"Addresse:\\n\\n \\n\",\n        \"groups\": [\n            {\n                \"description\": \"\",\n                \"id\": 4,\n                \"name\": \"Flerkamera\"\n            }\n        ],\n        \"active\": true,\n        \"id\": 15,\n        \"permissions\": [],\n        \"private_email\": \"andreashodneland@gmail.com\",\n        \"first_name\": \"Andreas\",\n        \"created\": \"2013-09-04T18:35:18.666272\",\n        \"birthdate\": null,\n        \"email\": \"andreas.hodneland@studentmediene.no\"\n    }\n]";

        List<Person> persons = personMapper.personsFromJson(json);

        assertEquals(3, persons.size());
        assertEquals("Kjøniksen Brucki", persons.get(0).getLastName());
        assertEquals("Haug", persons.get(1).getLastName());
    }
}
