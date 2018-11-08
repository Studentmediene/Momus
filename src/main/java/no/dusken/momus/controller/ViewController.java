package no.dusken.momus.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import no.dusken.momus.exceptions.RestException;

@Controller
public class ViewController {

    // Default 404 on all api endpoints so they wont be redirected to frontend
    @GetMapping("/api/**")
    public void api() {
        throw new RestException("Not found!", 404);
    }

    @GetMapping("/**")
    public String index(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8"); // Have to do this for some reason        
        // Forward to home page so that route is preserved.
        return "forward:/assets/index.html";
    }

    @GetMapping({"/beta/**"})
    public String betaIndex(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        return "forward:/assets/beta/index.html";
    }
}