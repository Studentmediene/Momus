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

package no.dusken.momus.article;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import no.dusken.momus.common.StaticValue;

@AllArgsConstructor
@Getter
public enum ArticleStatus {
    UNKNOWN("Ukjent", "#ffffff", true),
    PLANNED("Planlagt", "#eeeeee", true),
    WRITING("Skrives", "#aaaaff", true),
    DESKING("Deskes", "#ffaaaa", true),
    APPROVING("Til godkjenning", "#aaffaa", true),
    EDITING("Til korrektur", "#009F00", true),
    PUBLISHED("Publisert", "#00ff00", true);

    private String name;
    private String color;
    private boolean available;

    public static Map<String, StaticValue> map() {
        Map<String, StaticValue> statusNames = new LinkedHashMap<>();
        for(ArticleStatus r : ArticleStatus.values()) {
            Map<String, Object> extra = new HashMap<>();
            extra.put("color", r.color);
            statusNames.put(r.toString(), new StaticValue(r.name, extra));
        }

        return statusNames;
    }
}