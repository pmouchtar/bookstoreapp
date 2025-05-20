package com.petros.bookstore.service;

import com.petros.bookstore.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class JwtService {

    private final UserService userService;

    private final String issuer;

    private final Duration ttl;

    private final JwtEncoder jwtEncoder;

    public String generateToken(final String username) {

        User user = userService.getUserByUsername(username);
        Long userId = user.getId();

        final var claimsSet = JwtClaimsSet.builder()
                .subject(username)
                .issuer(issuer)
                .expiresAt(Instant.now().plus(ttl))
                .claim("userId", userId)
                .claim("roles", List.of("ROLE_" + user.getRole().name()))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet))
                .getTokenValue();
    }

}
