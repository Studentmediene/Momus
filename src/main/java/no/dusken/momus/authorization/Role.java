package no.dusken.momus.authorization;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

public enum Role implements Serializable, GrantedAuthority {
    ROLE_USER, ROLE_ADMIN, ROLE_ILLUSTRATOR;

    @Override
    public String getAuthority() {
        return name();
    }
}
