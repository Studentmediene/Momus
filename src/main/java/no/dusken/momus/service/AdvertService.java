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
import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.Advert;
import no.dusken.momus.service.repository.AdvertRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
public class AdvertService {

    private final AdvertRepository advertRepository;

    public AdvertService(AdvertRepository advertRepository) {
        this.advertRepository = advertRepository;
    }

    public Advert getAdvertById(Long id) {
        if(!advertRepository.exists(id)) {
            throw new RestException("Advert with id=" + id + " not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return advertRepository.findOne(id);
    }

    public Advert saveAdvert(Advert advert){
        Advert newAdvert = advertRepository.saveAndFlush(advert);
        log.info("Advert with id {} creatd with data: {}", newAdvert.getId(), newAdvert);
        return newAdvert;
    }

    public Advert updateAdvert(Advert advert) {
        log.info("Advert with id {} updated, data: {}", advert.getId(), advert);
        return advertRepository.saveAndFlush(advert);
    }

    public Advert updateComment(Long id, String comment) {
        Advert advert = getAdvertById(id);
        advert.setComment(comment);
        return updateAdvert(advert);
    }

}
