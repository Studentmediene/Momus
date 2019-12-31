package no.dusken.momus.person.authorization;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum Role implements Serializable, GrantedAuthority {
    // The enums are sorted in such a way that the most important roles are higher up,
    // in the way that if a user has sveral roles, it's the role that appears first
    // in this list that will be their "main role"
    ROLE_DESK_CHIEF("Deskesjef"),
    ROLE_DESKER("Desker"),

    ROLE_ILLUSTRATION_CHIEF("Illustrasjonssjef"),
    ROLE_ILLUSTRATOR("Illustratør"),

    ROLE_PHOTO_CHIEF("Fotosjef"),
    ROLE_PHOTOGRAPHER("Fotograf"),
    
    ROLE_MUSIC_EDITOR("Musikkredaktør"),
    ROLE_MUSIC_JOURNALIST("Musikkjournalist"),
    ROLE_CULTURE_EDITOR("Kulturredaktør"),
    ROLE_CULTURE_JOURNALIST("Kulturjournalist"),
    ROLE_NEWS_EDITOR("Nyhetsredaktør"),
    ROLE_NEWS_JOURNALIST("Nyhetsjournalist"),
    ROLE_FOFO_EDITOR("Forskning- og forbrukerredaktør"),
    ROLE_FOFO_JOURNALIST("Forskning- og forbrukerjournalist"),
    ROLE_REPORTING_EDITOR("Reportasjeredaktør"),
    ROLE_REPORTING_JOURNALIST("Reportasjejournalist"),
    ROLE_SPORT_EDITOR("Sportsredaktør"),
    ROLE_SPORT_JOURNALIST("Sportsjournalist"),
    ROLE_DEBATE_EDITOR("Debattredaktør"),

    ROLE_SUB_EDITOR("Underredaktør"),
    ROLE_UD_EDITOR("Redaktør Under Dusken"),
    ROLE_DEVELOPER("Utvikler"),
    ROLE_ADMIN("Administrator"),

    ROLE_USER("Bruker"),
    ;

    @Getter
    private final String prettyName;

    Role(String prettyName) {
        this.prettyName = prettyName;
    }

    @Override
    public String getAuthority() {
        return name();
    }

    public static Map<String, String> roleNameMap() {
        Map<String, String> roleNames = new HashMap<>();
        for(Role r : Role.values()) {
            roleNames.put(r.toString(), r.getPrettyName());
        }

        return roleNames;
    }
}
