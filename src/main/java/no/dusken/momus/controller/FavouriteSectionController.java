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

package no.dusken.momus.controller;

import no.dusken.momus.model.FavouriteSection;
import no.dusken.momus.service.FavouriteSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/favsection")
public class FavouriteSectionController {

    @Autowired
    private FavouriteSectionService favouriteSectionService;

    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    FavouriteSection getNoteForLoggedInUser() {
        return favouriteSectionService.getFavouriteSectionForLoggedInUser();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public
    @ResponseBody
    FavouriteSection saveNoteForLoggedInUser(@RequestBody FavouriteSection favouriteSection) {
        return favouriteSectionService.saveFavouriteSectionForLoggedInUser(favouriteSection);
    }
}
