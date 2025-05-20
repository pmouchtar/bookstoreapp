package com.petros.bookstore.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.AbstractPostgresContainerTest;
import com.petros.bookstore.dto.UserAdminUpdateRequest;
import com.petros.bookstore.model.User;
import com.petros.bookstore.model.enums.Role;
import com.petros.bookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for admin-only user management endpoints.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserAdminIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    private Long adminId;
    private Long normalUserId;

    private void mockAuthentication(Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        Jwt jwt = new Jwt(
                "fake-token",
                Instant.now(),
                Instant.now().plusSeconds(3_600),
                Map.of("alg", "none"),
                claims
        );

        List<GrantedAuthority> authorities = List.of(() -> "ROLE_" + role);
        JwtAuthenticationToken auth =
                new JwtAuthenticationToken(jwt, authorities, "user-" + userId);
        auth.setAuthenticated(true);

        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @BeforeEach
    void init() {
        userRepository.deleteAll();

        // admin user
        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("Boss");
        admin.setUsername("adminboss");
        admin.setPassword(passwordEncoder.encode("Admin123"));
        admin.setRole(Role.ADMIN);
        adminId = userRepository.save(admin).getId();

        // normal user
        User user = new User();
        user.setFirstName("Petros");
        user.setLastName("Papadopoulos");
        user.setUsername("petrosdev");
        user.setPassword(passwordEncoder.encode("Secure123"));
        user.setRole(Role.USER);
        normalUserId = userRepository.save(user).getId();
    }

    @Test
    void getAllUsers_AsAdmin_ShouldReturnPage() throws Exception {
        mockAuthentication(adminId, "ADMIN");

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void updateUser_AsAdmin_ShouldUpdateRole() throws Exception {
        mockAuthentication(adminId, "ADMIN");

        UserAdminUpdateRequest req = new UserAdminUpdateRequest();
        req.setFirstName("Updated");
        req.setLastName("User");
        req.setRole(Role.ADMIN); // promote

        mockMvc.perform(put("/users/{id}", normalUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void deleteUser_AsAdmin_ShouldRemoveUser() throws Exception {
        mockAuthentication(adminId, "ADMIN");

        mockMvc.perform(delete("/users/{id}", normalUserId))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(normalUserId)).isEmpty();
    }

    @Test
    void deleteNonExistingUser_AsAdmin_ShouldReturn404() throws Exception {
        mockAuthentication(adminId, "ADMIN");

        Long nonexistentId = 9_999L;

        mockMvc.perform(delete("/users/{id}", nonexistentId))
                .andExpect(status().isNotFound());
    }
}
