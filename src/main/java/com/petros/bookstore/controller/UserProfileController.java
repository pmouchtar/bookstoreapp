package com.petros.bookstore.controller;

import com.petros.bookstore.dto.UserProfileResponseDto;
import com.petros.bookstore.dto.UserProfileUpdateRequest;
import com.petros.bookstore.mapper.UserMapper;
import com.petros.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequiredArgsConstructor
public class UserProfileController {

  private final UserService userService;

  @GetMapping()
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<UserProfileResponseDto> getUserProfile(
      final Authentication authentication) {

    // to find the userId from username
    Jwt jwt = (Jwt) authentication.getPrincipal();
    Long userId = jwt.getClaim("userId");

    final var user = userService.findUserById(userId);

    return ResponseEntity.ok(user);
  }

  @PutMapping
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<UserProfileResponseDto> updateUserProfile(
      final Authentication authentication, @Valid @RequestBody UserProfileUpdateRequest request) {

    // to find the userId from username
    Jwt jwt = (Jwt) authentication.getPrincipal();
    Long userId = jwt.getClaim("userId");

    final var updatedUser = userService.updateUserProfile(userId, request);
    return ResponseEntity.ok(UserMapper.toUserProfileDto(updatedUser));
  }

  @DeleteMapping
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Void> deleteUserProfile(final Authentication authentication) {

    // to find the userId from username
    Jwt jwt = (Jwt) authentication.getPrincipal();
    Long userId = jwt.getClaim("userId");

    userService.deleteUserById(userId);
    return ResponseEntity.noContent().build();
  }
}
