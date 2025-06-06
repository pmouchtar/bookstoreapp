package com.petros.bookstore.service;

import com.petros.bookstore.model.User;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

/**
 * Service responsible for generating JWT (JSON Web Token) tokens for
 * authenticated users. It uses the user's details to embed claims like
 * username, user ID, and roles into the token payload.
 */
@RequiredArgsConstructor
public class JwtService {

    private final UserService userService;

    /** The issuer value to be included in the token, e.g. "bookstore-api". */
    private final String issuer;

    /** The time-to-live (TTL) duration for the token before it expires. */
    private final Duration ttl;

    /** The encoder used to sign and encode the JWT. */
    private final JwtEncoder jwtEncoder;

    /**
     * Generates a signed JWT token for the user with the given username. The token
     * includes the following claims:
     * <ul>
     * <li><b>sub</b>: the username</li>
     * <li><b>iss</b>: the configured issuer</li>
     * <li><b>exp</b>: the expiration time based on TTL</li>
     * <li><b>userId</b>: the user's unique ID</li>
     * <li><b>roles</b>: a list containing the user's role, prefixed with
     * "ROLE_"</li>
     * </ul>
     *
     * @param username
     *            the username of the authenticated user
     * @return a signed JWT token as a string
     */
    public String generateToken(final String username) {
        User user = userService.getUserByUsername(username);
        Long userId = user.getId();

        final var claimsSet = JwtClaimsSet.builder().subject(username).issuer(issuer).expiresAt(Instant.now().plus(ttl))
                .claim("userId", userId).claim("roles", //
                        List.of("ROLE_" + user.getRole().name()))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
}
