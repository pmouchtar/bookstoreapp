package com.petros.bookstore.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.AbstractPostgresContainerTest;
import com.petros.bookstore.dto.UserProfileUpdateRequest;
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


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserProfileIntegrationTest extends AbstractPostgresContainerTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = new User();
        user.setFirstName("Petros");
        user.setLastName("Papadopoulos");
        user.setUsername("petrosdev");
        user.setPassword(passwordEncoder.encode("Secure123"));
        user.setRole(Role.USER);

        testUserId = userRepository.save(user).getId();
    }


    //mockAuthentication is used because the controller works with (authentication). Otherwise, nullPointerException is thrown
    private void mockAuthenticationWithUserId(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        Jwt jwt = new Jwt(
                "fake-token",         // token value
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),  // headers
                claims
        );

        List<GrantedAuthority> authorities = List.of(() -> "ROLE_USER");

        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, authorities, "user-" + userId);
        auth.setAuthenticated(true);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }


    @Test
    void testGetUserProfile() throws Exception {
        mockAuthenticationWithUserId(testUserId);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("petrosdev"));
    }

    @Test
    void testUpdateUserProfile() throws Exception {
        mockAuthenticationWithUserId(testUserId);

        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("Name");
        updateRequest.setUsername("updateduser");
        updateRequest.setPassword("Newpass123");

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.username").value("updateduser"));
    }

    @Test
    void testDeleteUserProfile() throws Exception {
        mockAuthenticationWithUserId(testUserId);

        mockMvc.perform(delete("/users/me"))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(testUserId)).isEmpty();
    }

    @Test
    void testUpdateUserProfile_WithInvalidPassword_ShouldReturn400() throws Exception {
        mockAuthenticationWithUserId(testUserId);

        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
        updateRequest.setFirstName("Petros");
        updateRequest.setLastName("Papadopoulos");
        updateRequest.setUsername("petrosdev");
        updateRequest.setPassword("123"); // invalid password

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }
}



