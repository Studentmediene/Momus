package no.dusken.momus.authorization;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum Role implements Serializable, GrantedAuthority {
    // The enums are sorted in such a way that the most important roles are higher up,
    // in the way that if a user has several roles, it's the role that appears first
    // in this list that will be their "main role"
    DESK_CHIEF("Deskesjef"),
    DESKER("Desker"),

    ILLUSTRATION_CHIEF("Illustrasjonssjef"),
    ILLUSTRATOR("Illustratør"),

    PHOTO_CHIEF("Fotosjef"),
    PHOTOGRAPHER("Fotograf"),
    
    MUSIC_EDITOR("Musikkredaktør"),
    MUSIC_JOURNALIST("Musikkjournalist"),
    CULTURE_EDITOR("Kulturredaktør"),
    CULTURE_JOURNALIST("Kulturjournalist"),
    NEWS_EDITOR("Nyhetsredaktør"),
    NEWS_JOURNALIST("Nyhetsjournalist"),
    FOFO_EDITOR("Forskning- og forbrukerredaktør"),
    FOFO_JOURNALIST("Forskning- og forbrukerjournalist"),
    REPORTING_EDITOR("Reportasjeredaktør"),
    REPORTING_JOURNALIST("Reportasjejournalist"),
    SPORT_EDITOR("Sportsredaktør"),
    SPORT_JOURNALIST("Sportsjournalist"),
    DEBATE_EDITOR("Debattredaktør"),

    UD_EDITOR("Redaktør Under Dusken"),
    EDITOR("Underredaktør"),
    DEVELOPER("Utvikler"),
    ADMIN("Administrator"),

    USER("Bruker"),
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
