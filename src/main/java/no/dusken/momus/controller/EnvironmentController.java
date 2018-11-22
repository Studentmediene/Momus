package no.dusken.momus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/env")
public class EnvironmentController {

    @Autowired
    private Environment env;

    @GetMapping("/all")
    public Map<String, Object> allEnv() {
        Map<String, Object> values = new HashMap<>();
        values.put("version", env.getProperty("momus.version"));
        values.put("devmode", env.acceptsProfiles(Profiles.of("dev")));
        values.put("noAuth", env.acceptsProfiles(Profiles.of("noAuth")));
        return values;
    }
}
