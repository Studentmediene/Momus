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
        String json = "{\"username\": \"mats\", \"phone_number\": \"12345678\", \"last_name\": \"Svensson\", \"last_updated\": \"2013-09-15T12:51:38.139717\", \"about\": null, \"groups\": [{\"id\": 2, \"name\": \"admin\"}], \"active\": true, \"id\": 594, \"private_email\": null, \"first_name\": \"Mats\", \"created\": \"2013-09-15T12:51:38.139661\", \"birthdate\": null, \"email\": null}";
        Person person = personMapper.personFromJson(json);

        assertEquals("mats", person.getUsername());
        assertEquals("12345678", person.getPhoneNumber());
        assertEquals("Mats", person.getFirstName());
        assertEquals("Svensson", person.getLastName());

        Set<Group> groups = new HashSet<>();
        groups.add(new Group(2L, "admin"));

        assertEquals(groups, person.getGroups());
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
        String json = "{\"total_pages\": 1, \"objects\": [{\"username\": \"alebru\", \"phone_number\": \"92233281\", \"last_name\": \"Kj\\u00f8niksen Brucki\", \"last_updated\": \"2013-09-04T18:35:18.664302\", \"about\": \"Addresse:\\n\\n \\n\", \"groups\": [{\"id\": 7, \"name\": \"Produksjon\"}], \"active\": true, \"id\": 6, \"private_email\": \"\", \"first_name\": \"Alexander\", \"created\": \"2013-09-04T18:35:18.664292\", \"birthdate\": null, \"email\": \"alexander.brucki@stv.no\"}, {\"username\": \"amahau\", \"phone_number\": \"95175034\", \"last_name\": \"Haug\", \"last_updated\": \"2013-09-04T18:35:18.664884\", \"about\": \"Addresse:\\n\\n \\n\", \"groups\": [{\"id\": 8, \"name\": \"Serie\"}], \"active\": true, \"id\": 9, \"private_email\": \"amanda_j_haug@hotmail.com\", \"first_name\": \"Amanda J\\u00f8rgine\", \"created\": \"2013-09-04T18:35:18.664875\", \"birthdate\": null, \"email\": \"amanda.haug@stv.no\"}, {\"username\": \"andhod\", \"phone_number\": \"46416928\", \"last_name\": \"Hodneland\", \"last_updated\": \"2013-09-04T18:35:18.666294\", \"about\": \"Addresse:\\n\\n \\n\", \"groups\": [{\"id\": 4, \"name\": \"Flerkamera\"}], \"active\": true, \"id\": 15, \"private_email\": \"andreashodneland@gmail.com\", \"first_name\": \"Andreas\", \"created\": \"2013-09-04T18:35:18.666272\", \"birthdate\": null, \"email\": \"andreas.hodneland@stv.no\"}], \"num_results\": 3, \"page\": 1}";

        List<Person> persons = personMapper.personsFromJson(json, "objects");

        assertEquals(3, persons.size());
        assertEquals("alebru", persons.get(0).getUsername());
        assertEquals("Kjøniksen Brucki", persons.get(0).getLastName());
        assertEquals(1, persons.get(0).getGroups().size());
        assertTrue(persons.get(0).getGroups().contains(new Group(7L, "produksjon")));
    }

    @Test
    public void testPersonsFromJsonAtRoot() throws Exception {
        String json = "[{\"username\": \"alebru\", \"phone_number\": \"92233281\", \"last_name\": \"Kj\\u00f8niksen Brucki\", \"last_updated\": \"2013-09-04T18:35:18.664302\", \"about\": \"Addresse:\\n\\n \\n\", \"groups\": [{\"id\": 7, \"name\": \"Produksjon\"}], \"active\": true, \"id\": 6, \"private_email\": \"\", \"first_name\": \"Alexander\", \"created\": \"2013-09-04T18:35:18.664292\", \"birthdate\": null, \"email\": \"alexander.brucki@stv.no\"}, {\"username\": \"amahau\", \"phone_number\": \"95175034\", \"last_name\": \"Haug\", \"last_updated\": \"2013-09-04T18:35:18.664884\", \"about\": \"Addresse:\\n\\n \\n\", \"groups\": [{\"id\": 8, \"name\": \"Serie\"}], \"active\": true, \"id\": 9, \"private_email\": \"amanda_j_haug@hotmail.com\", \"first_name\": \"Amanda J\\u00f8rgine\", \"created\": \"2013-09-04T18:35:18.664875\", \"birthdate\": null, \"email\": \"amanda.haug@stv.no\"}, {\"username\": \"andhod\", \"phone_number\": \"46416928\", \"last_name\": \"Hodneland\", \"last_updated\": \"2013-09-04T18:35:18.666294\", \"about\": \"Addresse:\\n\\n \\n\", \"groups\": [{\"id\": 4, \"name\": \"Flerkamera\"}], \"active\": true, \"id\": 15, \"private_email\": \"andreashodneland@gmail.com\", \"first_name\": \"Andreas\", \"created\": \"2013-09-04T18:35:18.666272\", \"birthdate\": null, \"email\": \"andreas.hodneland@stv.no\"}]";

        List<Person> persons = personMapper.personsFromJson(json);

        assertEquals(3, persons.size());
        assertEquals("Kjøniksen Brucki", persons.get(0).getLastName());
        assertEquals("Haug", persons.get(1).getLastName());
    }
}
