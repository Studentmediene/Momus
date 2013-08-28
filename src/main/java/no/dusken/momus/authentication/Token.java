package no.dusken.momus.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class Token extends AbstractAuthenticationToken {

    public Token() {
        super(null);
        setAuthenticated(false);
    }

    @Override public Object getCredentials() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public Object getPrincipal() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
