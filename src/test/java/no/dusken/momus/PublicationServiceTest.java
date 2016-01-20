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

package no.dusken.momus;

import no.dusken.momus.model.Page;
import no.dusken.momus.model.Publication;
import no.dusken.momus.service.PublicationService;
import no.dusken.momus.service.repository.PageRepository;
import no.dusken.momus.service.repository.PublicationRepository;
import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Transactional
public class PublicationServiceTest extends AbstractTestRunner{
    @Autowired
    PublicationRepository publicationRepository;

    @Autowired
    PublicationService publicationService;

    @Autowired
    PageRepository pageRepository;

    private Publication publication;

    private Page page1;
    private Page page2;
    private Page page3;

    @Before
    public void setUp() throws Exception {

        publication = new Publication(1L);
        publication.setName("testpub");
        publication.setReleaseDate(new Date(8999));
        publication = publicationRepository.save(publication);
        List<Page> pages = new ArrayList<>();

        page1 = new Page(1L);
        page1.setPageNr(1);
        page1.setAdvertisement(false);
        page1.setNote("");
        page1.setPublication(publication);
        page1 = pageRepository.save(page1);
        pages.add(page1);

        page2 = new Page(2L);
        page2.setPageNr(2);
        page2.setPublication(publication);
        page2 = pageRepository.save(page2);
        pages.add(page2);

        page3 = new Page(3L);
        page3.setPageNr(3);
        page3.setPublication(publication);
        page3 = pageRepository.save(page3);
        pages.add(page3);

        publication.setPages(pages);
        publication = publicationRepository.save(publication);
    }

    @Test
    public void testUpdatePublicationMetadata() throws Exception{
        Publication pub = new Publication(publication.getId());
        pub.setName("justanupdatedpubname");
        pub = publicationRepository.save(pub);

        assertEquals("justanupdatedpubname",pub.getName());
    }

    @Test
    public void testUpdatePageMetadata() throws Exception{
        Page page = new Page(page1.getId());
    }
}
