// src/test/java/com/petros/bookstore/config/TestDummyJwtFilter.java
package com.petros.bookstore.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component     // φορτώνει μόνο στο profile "test" γιατί το ζητάει η TestSecurityConfig
public class TestDummyJwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String userIdHeader = request.getHeader("X-USER-ID");
        if (StringUtils.hasText(userIdHeader)) {
            long userId = Long.parseLong(userIdHeader);

            Jwt jwt = new Jwt(
                    "dummy-token",
                    Instant.now(),
                    Instant.now().plusSeconds(3_600),
                    Map.of("alg", "none"),
                    Map.of("userId", userId)
            );

            List<GrantedAuthority> authorities = List.of(() -> "ROLE_USER");
            Authentication auth = new JwtAuthenticationToken(jwt, authorities, "user-" + userId);

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
