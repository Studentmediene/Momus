/*
 * Copyright 2014 Studentmediene i Trondheim AS
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

import no.dusken.momus.authentication.UserLoginService;
import no.dusken.momus.model.FavouriteSection;
import no.dusken.momus.service.repository.FavouriteSectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavouriteSectionService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    FavouriteSectionRepository favouriteSectionRepository;

    @Autowired
    private UserLoginService userLoginService;

    public FavouriteSection getFavouriteSectionForLoggedInUser(){
        Long userId = userLoginService.getId();
        FavouriteSection favouriteSection = favouriteSectionRepository.findByOwner_Id(userId);

        return favouriteSection;
    }

    public FavouriteSection saveFavouriteSectionForLoggedInUser(FavouriteSection favouriteSection){
        Long userId = userLoginService.getId();
        FavouriteSection existing = favouriteSectionRepository.findByOwner_Id(userId);
        if(existing == null){
            existing = favouriteSection;
            existing.setOwner(userLoginService.getLoggedInUser());
        }
        existing.setOwner(userLoginService.getLoggedInUser());
        existing.setSection(favouriteSection.getSection());
        logger.debug(existing.getId() + " " + existing.getSection());

        return favouriteSectionRepository.saveAndFlush(existing);
    }
}