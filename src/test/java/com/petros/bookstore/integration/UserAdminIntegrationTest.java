package com.petros.bookstore.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.AbstractPostgresContainerTest;
import com.petros.bookstore.dto.UserAdminUpdateRequest;
import com.petros.bookstore.dto.UserProfileResponseDto;
import com.petros.bookstore.model.User;
import com.petros.bookstore.model.enums.Role;
import com.petros.bookstore.repository.UserRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

/** Integration tests for admin-only user-management endpoints using TestRestTemplate. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserAdminIntegrationTest extends AbstractPostgresContainerTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private ObjectMapper objectMapper;

  private Long adminId;
  private Long normalUserId;
  private HttpHeaders adminHeaders;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();

    // admin
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

    adminHeaders = new HttpHeaders();
    adminHeaders.add("X-USER-ID", adminId.toString()); // handled by DummyJwtFilter
  }

  @Test
  void getAllUsers_AsAdmin_ShouldReturnPage() {
    HttpEntity<Void> entity = new HttpEntity<>(adminHeaders);

    ResponseEntity<Map<String, Object>> response =
        restTemplate.exchange(
            "/users", HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    List<?> content = (List<?>) response.getBody().get("content");
    assertThat(content).hasSize(2);
  }

  @Test
  void getAllUsers_WithUsernameFilter_ShouldReturnOneUser() {
    HttpEntity<Void> entity = new HttpEntity<>(adminHeaders);

    ResponseEntity<Map<String, Object>> response =
        restTemplate.exchange(
            "/users?username=petrosdev",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    List<?> content = (List<?>) response.getBody().get("content");
    assertThat(content).hasSize(1);
  }

  @Test
  void updateUser_AsAdmin_ShouldPromoteUser() {
    UserAdminUpdateRequest req = new UserAdminUpdateRequest();
    req.setFirstName("Updated");
    req.setLastName("User");
    req.setRole(Role.ADMIN); // promote

    adminHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<UserAdminUpdateRequest> entity = new HttpEntity<>(req, adminHeaders);

    ResponseEntity<UserProfileResponseDto> response =
        restTemplate.exchange(
            "/users/{id}", HttpMethod.PUT, entity, UserProfileResponseDto.class, normalUserId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().role()).isEqualTo(Role.ADMIN);
    assertThat(response.getBody().firstName()).isEqualTo("Updated");
  }

  @Test
  void updateUser_NonExisting_ShouldReturn404() {
    long nonexistentId = 9_999L;

    UserAdminUpdateRequest req = new UserAdminUpdateRequest();
    req.setFirstName("Foo");
    req.setLastName("Bar");
    req.setRole(Role.USER);

    adminHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<UserAdminUpdateRequest> entity = new HttpEntity<>(req, adminHeaders);

    ResponseEntity<String> response =
        restTemplate.exchange("/users/{id}", HttpMethod.PUT, entity, String.class, nonexistentId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void updateUser_InvalidPayload_ShouldReturn400() throws Exception {
    // empty first / last name → @NotBlank should fail
    UserAdminUpdateRequest req = new UserAdminUpdateRequest();
    req.setFirstName("");
    req.setLastName("");
    req.setRole(Role.USER);

    adminHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity =
        new HttpEntity<>(objectMapper.writeValueAsString(req), adminHeaders);

    ResponseEntity<String> response =
        restTemplate.exchange("/users/{id}", HttpMethod.PUT, entity, String.class, normalUserId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void deleteUser_AsAdmin_ShouldRemoveUser() {
    HttpEntity<Void> entity = new HttpEntity<>(adminHeaders);

    ResponseEntity<Void> response =
        restTemplate.exchange("/users/{id}", HttpMethod.DELETE, entity, Void.class, normalUserId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(userRepository.findById(normalUserId)).isEmpty();
  }

  @Test
  void deleteUser_NonExisting_ShouldReturn404() {
    HttpEntity<Void> entity = new HttpEntity<>(adminHeaders);
    long nonexistentId = 8_888L;

    ResponseEntity<String> response =
        restTemplate.exchange(
            "/users/{id}", HttpMethod.DELETE, entity, String.class, nonexistentId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
