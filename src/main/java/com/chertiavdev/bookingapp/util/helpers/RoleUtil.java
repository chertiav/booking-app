package com.chertiavdev.bookingapp.util.helpers;

import org.springframework.security.core.GrantedAuthority;

public final class RoleUtil {
    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    private RoleUtil() {
    }

    public static boolean isAdminRole(GrantedAuthority role) {
        return role.getAuthority().equals(ADMIN_ROLE);
    }
}
