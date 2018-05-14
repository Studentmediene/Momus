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

import no.dusken.momus.exceptions.RestException;
import no.dusken.momus.model.*;
import no.dusken.momus.service.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdvertService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdvertRepository advertRepository;


    public Advert getAdvertById(Long id) {
        if(!advertRepository.exists(id)) {
            throw new RestException("Advert with id=" + id + " not found", HttpServletResponse.SC_NOT_FOUND);
        }
        return advertRepository.findOne(id);
    }

    public List<Advert> getAdvertsByIds(List<Long> ids) {
        if(ids == null) {
            return new ArrayList<>();
        }
        return ids.stream().map(this::getAdvertById).collect(Collectors.toList());
    }


    public Advert saveAdvert(Advert advert){
        Advert newAdvert = advertRepository.saveAndFlush(advert);
        logger.info("Advert with id {} creatd with data: {}", newAdvert.getId(), newAdvert.dump());
        return newAdvert;
    }

    public Advert updateAdvert(Advert advert) {
        logger.info("Advert with id {} updated, data: {}", advert.getId(), advert.dump());
        return advertRepository.saveAndFlush(advert);
    }

    public Advert updateComment(Long id, String comment) {
        Advert advert = getAdvertById(id);
        advert.setComment(comment);
        return updateAdvert(advert);
    }

}
