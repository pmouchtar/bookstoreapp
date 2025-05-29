package com.petros.bookstore.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.AbstractPostgresContainerTest;
import com.petros.bookstore.dto.UserDTO.UserProfileResponseDto;
import com.petros.bookstore.dto.UserDTO.UserProfileUpdateRequestDto;
import com.petros.bookstore.model.User;
import com.petros.bookstore.model.enums.Role;
import com.petros.bookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

/** Integration tests for user-only endpoints using TestRestTemplate. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserProfileIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Long testUserId;
    private HttpHeaders headers;

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

        headers = new HttpHeaders();
        headers.add("X-USER-ID", testUserId.toString()); // magic header for the DummyJwtFilter
    }

    @Test
    void testGetUserProfile() {
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UserProfileResponseDto> response = restTemplate.exchange("/users/me", HttpMethod.GET, entity,
                UserProfileResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("petrosdev");
    }

    @Test
    void testUpdateUserProfile() {
        UserProfileUpdateRequestDto updateRequest = new UserProfileUpdateRequestDto("Updated", "Name", "updateduser",
                "Newpass123");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserProfileUpdateRequestDto> entity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<UserProfileResponseDto> response = restTemplate.exchange("/users/me", HttpMethod.PUT, entity,
                UserProfileResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().firstName()).isEqualTo("Updated");
        assertThat(response.getBody().username()).isEqualTo("updateduser");
    }

    @Test
    void testDeleteUserProfile() {
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange("/users/me", HttpMethod.DELETE, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(userRepository.findById(testUserId)).isEmpty();
    }

    @Test
    void testUpdateUserProfile_WithInvalidPassword_ShouldReturn400() {
        UserProfileUpdateRequestDto updateRequest = new UserProfileUpdateRequestDto("Petros", "Papadopoulos",
                "petrosdev", "123");

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserProfileUpdateRequestDto> entity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange("/users/me", HttpMethod.PUT, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testUpdateUserProfile_ShouldReturn400_WhenMissingFields() {
        UserProfileUpdateRequestDto request = new UserProfileUpdateRequestDto("", null, null, null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserProfileUpdateRequestDto> entity = new HttpEntity<>(request, headers);

        var response = restTemplate.exchange("/users/me?userId=" + testUserId, HttpMethod.PUT, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
