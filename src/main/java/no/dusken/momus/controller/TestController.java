package no.dusken.momus.controller;

import no.dusken.momus.model.Shop;
import no.dusken.momus.service.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/str")
public class TestController {
    @Autowired
    public Shop shop;

    @Autowired
    ShopRepository shopRepository;

    @RequestMapping(value = "/r", method= RequestMethod.GET)
    public @ResponseBody Shop getSomething2() {
        return shop;
    }

    @RequestMapping("add")
    public @ResponseBody Shop addShop() {
        Shop myShop = new Shop();
        myShop.setName("newShop222333");
        myShop = shopRepository.saveAndFlush(myShop);
        return myShop;
    }

    @RequestMapping("see")
    public @ResponseBody List<Shop> getShops() {
        return shopRepository.findAll();
    }

    @RequestMapping("see/{id}")
    public @ResponseBody Shop getShop(@PathVariable Long id) {
        return shopRepository.findOne(id);
    }
}
