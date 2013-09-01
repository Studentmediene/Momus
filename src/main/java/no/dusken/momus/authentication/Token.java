package no.dusken.momus.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class Token extends AbstractAuthenticationToken {


    public Token() {
        super(null); // Todo fix
    }

    @Override public Object getCredentials() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public Object getPrincipal() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
