package no.dusken.momus.person.authorization;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;
import no.dusken.momus.common.StaticValue;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
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

    private final String name;

    @Override
    public String getAuthority() {
        return name();
    }

    public static Map<String, StaticValue> map() {
        Map<String, StaticValue> roleMap = new LinkedHashMap<>();
        for(Role r : Role.values()) {
            roleMap.put(r.toString(), new StaticValue(r.name, null));
        }

        return roleMap;
    }
}
