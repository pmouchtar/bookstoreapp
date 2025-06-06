package com.petros.bookstore.controller;

import com.petros.bookstore.dto.userdto.UserAdminUpdateRequestDto;
import com.petros.bookstore.dto.userdto.UserProfileResponseDto;
import com.petros.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin-only controller for managing user accounts. Allows administrators to
 * retrieve, search, update, and delete users.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserService userService;

    /**
     * Retrieves a paginated list of users, optionally filtered by username, first
     * name, or last name.
     *
     * @param username
     *            optional username filter
     * @param firstName
     *            optional first name filter
     * @param lastName
     *            optional last name filter
     * @param pageable
     *            pagination and sorting information
     * @return paginated list of user profiles
     */
    @GetMapping
    public ResponseEntity<Page<UserProfileResponseDto>> getAllUsers(//
            @RequestParam(required = false) String username, //
            @RequestParam(required = false) String firstName, //
            @RequestParam(required = false) String lastName, //
            Pageable pageable) {

        Page<UserProfileResponseDto> result = userService.searchUsers(//
                username, firstName, lastName, pageable);

        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves a specific user's profile by ID.
     *
     * @param userId
     *            the ID of the user
     * @return the user's profile
     */
    @GetMapping("/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileResponseDto> getUser(@PathVariable Long userId) {
        UserProfileResponseDto responseDto = userService.findUserById(userId);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Updates a specific user's profile based on the provided information.
     *
     * @param userId
     *            the ID of the user to update
     * @param request
     *            the updated user information
     * @return the updated user profile
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDto> updateUser(@PathVariable Long userId,
            @Valid @RequestBody UserAdminUpdateRequestDto request) {

        UserProfileResponseDto updatedUser = userService.updateUserById(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId
     *            the ID of the user to delete
     * @return 204 No Content if deletion was successful
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
}
