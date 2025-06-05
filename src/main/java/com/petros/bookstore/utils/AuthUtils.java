package com.petros.bookstore.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Utility class for extracting authentication-related information from the
 * Spring Security context. This class is used to retrieve the authenticated
 * user's ID from the JWT token stored in the SecurityContext.
 */
@Component
public class AuthUtils {

    /**
     * Extracts the user ID from the JWT token present in the current
     * SecurityContext.
     *
     * @return the user ID as a {@link Long}, extracted from the "userId" claim of
     *         the JWT principal.
     * @throws ClassCastException
     *             if the principal is not of type {@link Jwt}
     * @throws NullPointerException
     *             if there is no authentication in context
     */
    public Long extractUserId() {
        final Authentication authentication = //
                SecurityContextHolder.getContext().getAuthentication();
        return ((Jwt) authentication.getPrincipal()).getClaim("userId");
    }
}
