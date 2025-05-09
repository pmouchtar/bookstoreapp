package com.petros.bookstore.controller;

import com.petros.bookstore.dto.UserProfileResponseDto;
import com.petros.bookstore.dto.UserProfileUpdateRequest;
import com.petros.bookstore.mapper.UserMapper;
import com.petros.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    //private final UserMapper userMapper;

    @GetMapping()
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(
            final Authentication authentication) {

        final var user =
                userService.getUserByUsername(authentication.getName());

        return ResponseEntity.ok(UserMapper.toUserProfileDto(user));
    }

    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileResponseDto> updateUserProfile(
            final Authentication authentication,
            @Valid  @RequestBody UserProfileUpdateRequest request) {

        final var updatedUser = userService.updateUserProfile(authentication.getName(), request);
        return ResponseEntity.ok(UserMapper.toUserProfileDto(updatedUser));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteUserProfile(final Authentication authentication) {
        userService.deleteUserByUsername(authentication.getName());
        return ResponseEntity.noContent().build();
    }

}