package com.petros.bookstore.controller;

import com.petros.bookstore.dto.UserDTO.UserProfileResponseDto;
import com.petros.bookstore.dto.UserDTO.UserProfileUpdateRequestDto;
import com.petros.bookstore.mapper.UserMapper;
import com.petros.bookstore.service.UserService;
import com.petros.bookstore.utils.AuthUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    private final AuthUtils authUtils = new AuthUtils();

    private Long userId;

    @GetMapping()
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileResponseDto> getUserProfile() {

        userId = authUtils.extractUserId();
        final var user = userService.findUserById(userId);

        return ResponseEntity.ok(user);
    }

    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileResponseDto> updateUserProfile(
            @Valid @RequestBody UserProfileUpdateRequestDto request) {

        userId = authUtils.extractUserId();
        final var updatedUser = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(UserMapper.toUserProfileDto(updatedUser));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteUserProfile() {

        userId = authUtils.extractUserId();
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
}
