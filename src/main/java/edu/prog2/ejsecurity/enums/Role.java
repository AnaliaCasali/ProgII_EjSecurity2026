package edu.prog2.ejsecurity.enums;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    USER, ADMIN;

    @Override
    public @Nullable String getAuthority() {
        return "ROLE_" + name();
    }
}
