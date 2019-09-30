package no.dusken.momus.authorization;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize("hasAnyRole(T(no.dusken.momus.authorization.Role).ADMIN)")
public @interface AdminAuthorization {
}
