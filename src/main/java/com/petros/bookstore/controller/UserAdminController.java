package com.petros.bookstore.controller;

import com.petros.bookstore.dto.*;
import com.petros.bookstore.mapper.UserMapper;
import com.petros.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<Page<UserProfileResponseDto>> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            Pageable pageable
    ) {
        if (username != null || firstName != null || lastName != null) {
            return ResponseEntity.ok(userService.searchUsers(username, firstName, lastName, pageable));
        } else {
            return ResponseEntity.ok(userService.findAll(pageable));
        }
    }

    @GetMapping("/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileResponseDto> getUser(@PathVariable Long userId) {
        UserProfileResponseDto responseDto = userService.findUserById(userId);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDto> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserAdminUpdateRequest request) {
        UserProfileResponseDto updatedUser = userService.updateUserById(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        boolean deleted = userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
}
