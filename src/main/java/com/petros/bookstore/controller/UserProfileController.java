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

/**
 * Controller for handling authenticated user profile actions.
 * Allows users (with USER or ADMIN roles) to view, update, or delete their own account.
 */
@RestController
@RequestMapping("/users/me")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final AuthUtils authUtils;

    /**
     * Retrieves the authenticated user's profile.
     *
     * @return the current user's profile
     */
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileResponseDto> getUserProfile() {
        Long userId = authUtils.extractUserId();
        var user = userService.findUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Updates the authenticated user's profile.
     *
     * @param request the new profile data
     * @return the updated user profile
     */
    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileResponseDto> updateUserProfile(
            @Valid @RequestBody UserProfileUpdateRequestDto request) {

        Long userId = authUtils.extractUserId();
        var updatedUser = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(UserMapper.toUserProfileDto(updatedUser));
    }

    /**
     * Deletes the authenticated user's account.
     *
     * @return 204 No Content if deletion was successful
     */
    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteUserProfile() {
        Long userId = authUtils.extractUserId();
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
}
