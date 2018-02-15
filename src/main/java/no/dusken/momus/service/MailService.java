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

import com.google.api.services.drive.model.File;
import no.dusken.momus.authentication.UserDetailsService;
import no.dusken.momus.exceptions.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class MailService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public JavaMailSenderImpl mailSender;
 
    public void sendSimpleMessage(
	 String to, String subject, String text) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            message.setRecipients(Message.RecipientType.TO, to);
            message.setSubject(subject);
            message.setContent(text, "text/html");
            mailSender.send(message);
        } catch(MessagingException e) {
            logger.warn(e.toString());
            return;
        }
    }
}

