package no.dusken.momus.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.env.Environment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import no.dusken.momus.authorization.Role;

@AllArgsConstructor
@Getter
public class GroupRole {
    public String group;
    public Role role;

    public static List<GroupRole> getGroupRoleMapping(Environment env) {
        return new ArrayList<>(Arrays.asList(
            new GroupRole(env.getProperty("ldap.groups.ud-editor"), Role.ADMIN),
            new GroupRole(env.getProperty("ldap.groups.ud-editor"), Role.UD_EDITOR),

            new GroupRole(env.getProperty("ldap.groups.developer"), Role.ADMIN),
            new GroupRole(env.getProperty("ldap.groups.developer"), Role.DEVELOPER),

            new GroupRole(env.getProperty("ldap.groups.desk-chief"), Role.DESK_CHIEF),
            new GroupRole(env.getProperty("ldap.groups.desker"), Role.DESKER),

            new GroupRole(env.getProperty("ldap.groups.illustration-chief"), Role.ILLUSTRATION_CHIEF),
            new GroupRole(env.getProperty("ldap.groups.illustrator"), Role.ILLUSTRATOR),

            new GroupRole(env.getProperty("ldap.groups.photo-chief"), Role.PHOTO_CHIEF),
            new GroupRole(env.getProperty("ldap.groups.photographer"), Role.PHOTOGRAPHER),

            new GroupRole(env.getProperty("ldap.groups.music-editor"), Role.MUSIC_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.music-editor"), Role.EDITOR),
            new GroupRole(env.getProperty("ldap.groups.music-journalist"), Role.MUSIC_JOURNALIST),

            new GroupRole(env.getProperty("ldap.groups.culture-editor"), Role.CULTURE_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.culture-editor"), Role.EDITOR),
            new GroupRole(env.getProperty("ldap.groups.culture-journalist"), Role.CULTURE_JOURNALIST),

            new GroupRole(env.getProperty("ldap.groups.news-editor"), Role.NEWS_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.news-editor"), Role.EDITOR),
            new GroupRole(env.getProperty("ldap.groups.news-journalist"), Role.NEWS_JOURNALIST),

            new GroupRole(env.getProperty("ldap.groups.sport-editor"), Role.SPORT_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.sport-editor"), Role.EDITOR),
            new GroupRole(env.getProperty("ldap.groups.sport-journalist"), Role.SPORT_JOURNALIST),

            new GroupRole(env.getProperty("ldap.groups.reporting-editor"), Role.REPORTING_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.reporting-editor"), Role.EDITOR),
            new GroupRole(env.getProperty("ldap.groups.reporting-journalist"), Role.REPORTING_JOURNALIST),

            new GroupRole(env.getProperty("ldap.groups.fofo-editor"), Role.FOFO_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.fofo-editor"), Role.EDITOR),
            new GroupRole(env.getProperty("ldap.groups.fofo-journalist"), Role.FOFO_JOURNALIST),

            new GroupRole(env.getProperty("ldap.groups.debate-editor"), Role.DEBATE_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.debate-editor"), Role.EDITOR)
        ));
    }
}