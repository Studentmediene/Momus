package no.dusken.momus.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.dusken.momus.person.authorization.Role;

@RestController
@RequestMapping("/api/static-values")
public class StaticValuesController {
    private final Environment env;

    private final Map<String, String> roleNames;

    public StaticValuesController(Environment env) {
        this.env = env;
        roleNames = Role.roleNameMap();
    }

    @GetMapping("/role-names")
    public Map<String, String> getRoleNames() {
        return roleNames;
    }

    @GetMapping("/env")
    public Map<String, Object> getEnvironment() {
        Map<String, Object> values = new HashMap<>();
        values.put("version", env.getProperty("momus.version"));
        values.put("devmode", env.acceptsProfiles(Profiles.of("dev")));
        values.put("noAuth", env.acceptsProfiles(Profiles.of("noAuth")));
        return values;
    }
}
