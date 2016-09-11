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

import no.dusken.momus.authentication.UserLoginService;
import no.dusken.momus.model.LandingPage;
import no.dusken.momus.model.Person;
import no.dusken.momus.service.repository.LandingPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LandingPageService {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private LandingPageRepository landingPageRepository;

    public LandingPage saveLandingPage(String landingPage){
        System.out.println(landingPage);
        Person user = userLoginService.getLoggedInUser();

        LandingPage existing = landingPageRepository.findByOwner(user);

        if(existing == null){
            LandingPage page = new LandingPage();
            page.setPage(landingPage);
            existing = page;
        }
        existing.setOwner(user);
        existing.setPage(landingPage);

        return landingPageRepository.saveAndFlush(existing);

    }
}
