package no.dusken.momus.service;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import no.dusken.momus.model.Person;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Iterator;

@Service
public class MailService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MailSender mailSender;

    @Autowired
    private SimpleMailMessage tempMsg;

    public void setMailSender(MailSender mailSender){
        this.mailSender = mailSender;
    }

    public void setTempMsg(SimpleMailMessage tempMsg) {
        this.tempMsg = tempMsg;
    }

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

        logger.info("E-poster mottat: " + String.valueOf(server.getReceivedEmailSize()));
        Iterator emailIter = server.getReceivedEmail();
        SmtpMessage email = (SmtpMessage)emailIter.next();
        logger.info(email.toString());

        return result;

    }
}
