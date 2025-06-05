package com.petros.bookstore.service;

import com.petros.bookstore.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserDetailsService} that loads user-specific data
 * from the database using JPA.
 * This service is used by Spring Security during the authentication process to
 * retrieve user details such as username, password, and authorities (roles).
 */
@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user from the database by their username.
     *
     * @param username
     *            The username identifying the user whose data is required.
     * @return A fully populated {@link UserDetails} object containing the user's
     *         credentials and roles.
     * @throws UsernameNotFoundException
     *             If no user is found with the given username.
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> User.builder().username(username).password(user.getPassword())
                        .authorities(List.of(new SimpleGrantedAuthority(//
                                "ROLE_" + user.getRole().name()))).build())
                .orElseThrow(
                        () -> new UsernameNotFoundException(//
                                "User with username [%s] not found".formatted(username)));
    }
}
