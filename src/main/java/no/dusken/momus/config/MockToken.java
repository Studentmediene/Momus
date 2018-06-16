package no.dusken.momus.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class MockToken extends AbstractAuthenticationToken {

    @Getter @Setter private String username;
    private UserDetails principal;

    public MockToken(String username) {
        super(null);
        this.username = username;
    }

    public MockToken(String username, UserDetails principal) {
        super(principal.getAuthorities());
        this.username = username;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
