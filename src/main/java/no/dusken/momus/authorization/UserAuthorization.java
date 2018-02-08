package no.dusken.momus.authorization;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize("hasRole(T(no.dusken.momus.authorization.Role).ROLE_USER)")
public @interface UserAuthorization {
}
