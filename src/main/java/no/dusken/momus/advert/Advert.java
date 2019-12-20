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

package no.dusken.momus.advert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import no.dusken.momus.messaging.Messageable;
import no.dusken.momus.common.AbstractEntity;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@ToString(of = {"name"}, callSuper = true)
@Builder(toBuilder = true)
public class Advert extends AbstractEntity implements Messageable {
    private String name;

    private String comment;

    @Override
    @JsonIgnore
    public List<String> getDestinations() {
        return Arrays.asList(
                "/ws/adverts",
                "/ws/adverts/" + id
        );
    }
}
