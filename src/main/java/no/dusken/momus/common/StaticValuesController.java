package no.dusken.momus.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.dusken.momus.article.ArticleReviewStatus;
import no.dusken.momus.article.ArticleStatus;
import no.dusken.momus.person.authorization.Role;
import no.dusken.momus.publication.LayoutStatus;

@RestController
@RequestMapping("/api/static-values")
public class StaticValuesController {
    private final Environment env;

    public StaticValuesController(Environment env) {
        this.env = env;
    }

    @GetMapping("/role")
    public Map<String, StaticValue> getRoles() {
        return Role.map();
    }

    @GetMapping("/article-status")
    public Map<String, StaticValue> getArticleStatus() {
        return ArticleStatus.map();
    }

    @GetMapping("/article-review-status")
    public Map<String, StaticValue> getArticleReviewStatus() {
        return ArticleReviewStatus.map();
    }

    @GetMapping("/layout-status")
    public Map<String, StaticValue> getLayoutStatus() {
        return LayoutStatus.map();
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
