package no.dusken.momus.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping({"", "/", "/{[path:(?!assets|api).*}/**"})
    public String redirect(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8"); // Have to do this for some reason        
        // Forward to home page so that route is preserved.
        return "forward:/assets/index.html";
    }

    @GetMapping({"/beta", "/beta/", "/beta/{[path:(?!assets).*}/**"})
    public String beta(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        return "forward:/assets/beta/index.html";
    }
}