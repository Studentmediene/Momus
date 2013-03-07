package no.dusken.momus.controller;

import no.dusken.momus.model.Shop;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Mats Svensson
 */
@Controller
@RequestMapping("/str")
public class TestController {

    @RequestMapping("/r")
    public @ResponseBody Shop getSomething2() {
        Shop shop = new Shop();
        shop.setId(15);
        shop.setName("ShopName");

        return shop;
    }

    @RequestMapping("redir")
    public String goAway() {
        return "r";
    }
}
