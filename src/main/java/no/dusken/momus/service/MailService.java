package no.dusken.momus.service;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import no.dusken.momus.model.Person;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Iterator;

@Service
public class MailService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private SimpleMailMessage tempMsg;

    public String sendMail(){
        SimpleSmtpServer server = SimpleSmtpServer.start();
        SimpleMailMessage msg = new SimpleMailMessage(this.tempMsg);
        msg.setTo("egrimstad95@gmail.com");
        msg.setText("Dette er en e-post sendt fra Momus.");
        String result;
        try{
            mailSender.send(msg);
            result = "Mail sent";
        }catch (MailException e){
            logger.info(e.getMessage());
            logger.info(msg.getText());
            result = "Could not send";
        }

        server.stop();

        logger.info("E-poster mottatt: " + String.valueOf(server.getReceivedEmailSize()));
        Iterator emailIter = server.getReceivedEmail();
        SmtpMessage email = (SmtpMessage)emailIter.next();
        logger.info(email.toString());

        return result;

    }

    public String sendHTMLMail(){
        SimpleSmtpServer server = SimpleSmtpServer.start();

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
        } catch (MessagingException e) {
            logger.warn(e.toString());
            return "error";
        }
        String msg = "<h3>Halla</h3><p>Dette er en e-post fra Momus</p>";
        try{
            mimeMessage.setContent(msg, "text/html");
            helper.setTo("egrimstad95@gmail.com");
            helper.setSubject("E-post fra Momus");
            helper.setFrom("momus@smint.no");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.warn(e.toString());
            return "error";
        }
        server.stop();

        logger.info("E-poster mottatt: " + String.valueOf(server.getReceivedEmailSize()));
        Iterator emailIter = server.getReceivedEmail();
        SmtpMessage email = (SmtpMessage)emailIter.next();
        logger.info(email.toString());

        return "hei";
    }
}
