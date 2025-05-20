package com.petros.bookstore.service;

import com.petros.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {

        return userRepository.findByUsername(username).map(user ->
                User.builder()
                        .username(username)
                        .password(user.getPassword())
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                        .build()
        ).orElseThrow(() -> new UsernameNotFoundException(
                "User with username [%s] not found".formatted(username)));
    }

}
