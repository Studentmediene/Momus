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
        String json = "{\"description\": \"De som driver med det tekniske for videoproduksjon.\", \"id\": 1, \"name\": \"videoteknisk\"}";
        Group group = groupMapper.groupFromJson(json);

        assertEquals(1L, group.getId().longValue());
        assertEquals("videoteknisk", group.getName());
        assertEquals("De som driver med det tekniske for videoproduksjon.", group.getDescription());
    }

    @Test
    public void testNoGroupForInvalidJSon() {
        String json = "{lol: 'haha'}";
        Group group = groupMapper.groupFromJson(json);

        assertTrue(group == null);
    }

    @Test
    public void testGroupsFromJson() {
        String json = "{\"total_pages\": 1, \"objects\": [\n    {\n        \"description\": \"De som driver med det tekniske for videoproduksjon.\",\n        \"id\": 1,\n        \"name\": \"videoteknisk\"\n    },\n    {\n        \"description\": \"Administrasjonen i Student-TV er ansvarlig for den daglige driften og best\\u00e5r av  ansvarlig redakt\\u00f8r, daglig leder og \\u00f8konomiansvarlig. \\r\\n\\r\\nDaglig leder i Student-TV er gjengsjef ovenfor Studentersamfundet og  representerer Student-TV i gjengsjefkollegiet. Daglig leder er ogs\\u00e5  sammen med ansvarlig redakt\\u00f8r personalansvarlig i Student-TV.\\r\\n\\r\\n\\u00d8konomiansvarlig f\\u00f8rer Student-TVs regnskap, og setter opp budsjett i samr\\u00e5d med daglig leder.\\r\\n\\r\\nAnsvarlig  redakt\\u00f8r st\\u00e5r etisk og rettslig ansvarlig for innholdet som publiseres av Student-TV p\\u00e5 www.stv.no.\",\n        \"id\": 2,\n        \"name\": \"admin\"\n    },\n    {\n        \"description\": \"I Student-TVs markedsavdeling finner du tretten ildsjeler som brenner for \\u00e5 fronte Student-TV utad. Vi har seks eksternprodusenter som har kontakt med n\\u00e6ringslivet gjennom oppdragsfilm. Vi har fire kreative grafikere som ser til av v\\u00e5r grafiske profil p\\u00e5 trykk og video holder m\\u00e5l, og tre markedsf\\u00f8rere som s\\u00f8rger for at alle Trondheimsstudentene vet hvor de skal finne spennende nyheter, kultur og underholdning.\",\n        \"id\": 3,\n        \"name\": \"marked\"\n    },\n    {\n        \"description\": \"Humorredaksjonen i Student-TV s\\u00f8rger hver uke for \\u00e5 berike samfunnsdebatten med satiriske innslag om aktuelle tema. Spalten heter Apropos, og der finner du rompehumor, ordspill og sexvitser for en god sak! Vi lager ogs\\u00e5 innslag som er helt usaklig, for en d\\u00e5rlig sak eller som handler om saksing. Disse finner du i spalten Sketsjup. God ford\\u00f8yelse.\",\n        \"id\": 5,\n        \"name\": \"Humor\"\n    },\n    {\n        \"description\": \"STV Produksjon best\\u00e5r av eksternprodusenter og motiongrafikere. Sammen lager vi oppdragsfilm for eksterne akt\\u00f8rer. Vi har erfaring med produksjon av reklamefilm, film av arrangement og musikkvideo for \\u00e5 nevne noen.\",\n        \"id\": 7,\n        \"name\": \"Produksjon\"\n    },\n    {\n        \"description\": \"\",\n        \"id\": 9,\n        \"name\": \"Feature\"\n    },\n    {\n        \"description\": null,\n        \"id\": 11,\n        \"name\": \"Panger\"\n    },\n    {\n        \"description\": \"De som fikser alt\",\n        \"id\": 12,\n        \"name\": \"IT\"\n    },\n    {\n        \"description\": \"Avis-sjefer\",\n        \"id\": 14,\n        \"name\": \"Redakt\\u00f8r\"\n    }\n], \"num_results\": 9, \"page\": 1}";
        List<Group> groups = groupMapper.groupsFromJson(json, "objects");

        assertEquals(9, groups.size());

        assertEquals("videoteknisk", groups.get(0).getName());
        assertEquals(1L, groups.get(0).getId().longValue());
        assertEquals("De som driver med det tekniske for videoproduksjon.", groups.get(0).getDescription());

        assertEquals("Feature", groups.get(5).getName());
        assertEquals(9L, groups.get(5).getId().longValue());
        assertEquals("", groups.get(5).getDescription());

        assertEquals("Panger", groups.get(6).getName());
        assertEquals(11L, groups.get(6).getId().longValue());
        assertEquals(null, groups.get(6).getDescription());
    }

    @Test
    public void testGroupsFromJsonRoot() {
        String json = "[\n    {\n        \"description\": \"De som driver med det tekniske for videoproduksjon.\",\n        \"id\": 1,\n        \"name\": \"videoteknisk\"\n    },\n    {\n        \"description\": \"Administrasjonen i Student-TV er ansvarlig for den daglige driften og best\\u00e5r av  ansvarlig redakt\\u00f8r, daglig leder og \\u00f8konomiansvarlig. \\r\\n\\r\\nDaglig leder i Student-TV er gjengsjef ovenfor Studentersamfundet og  representerer Student-TV i gjengsjefkollegiet. Daglig leder er ogs\\u00e5  sammen med ansvarlig redakt\\u00f8r personalansvarlig i Student-TV.\\r\\n\\r\\n\\u00d8konomiansvarlig f\\u00f8rer Student-TVs regnskap, og setter opp budsjett i samr\\u00e5d med daglig leder.\\r\\n\\r\\nAnsvarlig  redakt\\u00f8r st\\u00e5r etisk og rettslig ansvarlig for innholdet som publiseres av Student-TV p\\u00e5 www.stv.no.\",\n        \"id\": 2,\n        \"name\": \"admin\"\n    },\n    {\n        \"description\": \"I Student-TVs markedsavdeling finner du tretten ildsjeler som brenner for \\u00e5 fronte Student-TV utad. Vi har seks eksternprodusenter som har kontakt med n\\u00e6ringslivet gjennom oppdragsfilm. Vi har fire kreative grafikere som ser til av v\\u00e5r grafiske profil p\\u00e5 trykk og video holder m\\u00e5l, og tre markedsf\\u00f8rere som s\\u00f8rger for at alle Trondheimsstudentene vet hvor de skal finne spennende nyheter, kultur og underholdning.\",\n        \"id\": 3,\n        \"name\": \"marked\"\n    },\n    {\n        \"description\": \"Humorredaksjonen i Student-TV s\\u00f8rger hver uke for \\u00e5 berike samfunnsdebatten med satiriske innslag om aktuelle tema. Spalten heter Apropos, og der finner du rompehumor, ordspill og sexvitser for en god sak! Vi lager ogs\\u00e5 innslag som er helt usaklig, for en d\\u00e5rlig sak eller som handler om saksing. Disse finner du i spalten Sketsjup. God ford\\u00f8yelse.\",\n        \"id\": 5,\n        \"name\": \"Humor\"\n    },\n    {\n        \"description\": \"STV Produksjon best\\u00e5r av eksternprodusenter og motiongrafikere. Sammen lager vi oppdragsfilm for eksterne akt\\u00f8rer. Vi har erfaring med produksjon av reklamefilm, film av arrangement og musikkvideo for \\u00e5 nevne noen.\",\n        \"id\": 7,\n        \"name\": \"Produksjon\"\n    },\n    {\n        \"description\": \"\",\n        \"id\": 9,\n        \"name\": \"Feature\"\n    },\n    {\n        \"description\": null,\n        \"id\": 11,\n        \"name\": \"Panger\"\n    },\n    {\n        \"description\": \"De som fikser alt\",\n        \"id\": 12,\n        \"name\": \"IT\"\n    },\n    {\n        \"description\": \"Avis-sjefer\",\n        \"id\": 14,\n        \"name\": \"Redakt\\u00f8r\"\n    }\n]";
        List<Group> groups = groupMapper.groupsFromJson(json);

        assertEquals(9, groups.size());

        assertEquals("videoteknisk", groups.get(0).getName());
        assertEquals(1L, groups.get(0).getId().longValue());
        assertEquals("De som driver med det tekniske for videoproduksjon.", groups.get(0).getDescription());

        assertEquals("Feature", groups.get(5).getName());
        assertEquals(9L, groups.get(5).getId().longValue());
        assertEquals("", groups.get(5).getDescription());

        assertEquals("Panger", groups.get(6).getName());
        assertEquals(11L, groups.get(6).getId().longValue());
        assertEquals(null, groups.get(6).getDescription());
    }

}
