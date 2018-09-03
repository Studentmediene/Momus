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

import no.dusken.momus.authentication.UserDetailsService;
import no.dusken.momus.model.Person;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import static org.mockito.Mockito.*;

import no.dusken.momus.config.TestConfig;

import java.util.UUID;

/**
 * Values needed for most of the test cases
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public abstract class AbstractServiceTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    protected MessagingService messagingService;

    public void userMockSetup() {
        MockitoAnnotations.initMocks(this);
        Person me = Person.builder()
                .id(1L)
                .guid(UUID.randomUUID())
                .username("ei")
                .name("Eivind")
                .email("ei@vi.nd")
                .phoneNumber("12345678")
                .active(true)
                .build();

        doNothing().when(messagingService).broadcastEntityAction(any(), any());
        when(userDetailsService.getLoggedInPerson()).thenReturn(me);
    }
}
