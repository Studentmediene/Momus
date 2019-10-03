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
            new GroupRole(env.getProperty("ldap.groups.ud-editor"), Role.ROLE_ADMIN),
            new GroupRole(env.getProperty("ldap.groups.ud-editor"), Role.ROLE_UD_EDITOR),

            new GroupRole(env.getProperty("ldap.groups.developer"), Role.ROLE_ADMIN),
            new GroupRole(env.getProperty("ldap.groups.developer"), Role.ROLE_DEVELOPER),

            new GroupRole(env.getProperty("ldap.groups.desk-chief"), Role.ROLE_DESK_CHIEF),
            new GroupRole(env.getProperty("ldap.groups.desker"), Role.ROLE_DESKER),

            new GroupRole(env.getProperty("ldap.groups.illustration-chief"), Role.ROLE_ILLUSTRATION_CHIEF),
            new GroupRole(env.getProperty("ldap.groups.illustrator"), Role.ROLE_ILLUSTRATOR),

            new GroupRole(env.getProperty("ldap.groups.photo-chief"), Role.ROLE_PHOTO_CHIEF),
            new GroupRole(env.getProperty("ldap.groups.photographer"), Role.ROLE_PHOTOGRAPHER),

            new GroupRole(env.getProperty("ldap.groups.music-editor"), Role.ROLE_MUSIC_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.music-editor"), Role.ROLE_SUB_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.music-journalist"), Role.ROLE_MUSIC_JOURNALIST),

            new GroupRole(env.getProperty("ldap.groups.culture-editor"), Role.ROLE_CULTURE_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.culture-editor"), Role.ROLE_SUB_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.culture-journalist"), Role.ROLE_CULTURE_JOURNALIST),

            new GroupRole(env.getProperty("ldap.groups.news-editor"), Role.ROLE_NEWS_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.news-editor"), Role.ROLE_SUB_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.news-journalist"), Role.ROLE_NEWS_JOURNALIST),

            new GroupRole(env.getProperty("ldap.groups.sport-editor"), Role.ROLE_SPORT_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.sport-editor"), Role.ROLE_SUB_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.sport-journalist"), Role.ROLE_SPORT_JOURNALIST),

            new GroupRole(env.getProperty("ldap.groups.reporting-editor"), Role.ROLE_REPORTING_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.reporting-editor"), Role.ROLE_SUB_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.reporting-journalist"), Role.ROLE_REPORTING_JOURNALIST),

            new GroupRole(env.getProperty("ldap.groups.fofo-editor"), Role.ROLE_FOFO_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.fofo-editor"), Role.ROLE_SUB_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.fofo-journalist"), Role.ROLE_FOFO_JOURNALIST),

            new GroupRole(env.getProperty("ldap.groups.debate-editor"), Role.ROLE_DEBATE_EDITOR),
            new GroupRole(env.getProperty("ldap.groups.debate-editor"), Role.ROLE_SUB_EDITOR)
        ));
    }
}