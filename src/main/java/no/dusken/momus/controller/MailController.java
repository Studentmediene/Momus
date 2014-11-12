package no.dusken.momus.controller;

import no.dusken.momus.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/mail")
public class MailController {

    @Autowired
    MailService mailService;

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public @ResponseBody String sendMail(){
        return mailService.sendHTMLMail();
    }
}
