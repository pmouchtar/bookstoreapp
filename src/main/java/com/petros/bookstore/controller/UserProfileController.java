package com.petros.bookstore.controller;

import com.petros.bookstore.dto.UserProfileDto;
import com.petros.bookstore.mapper.UserMapper;
import com.petros.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserProfileDto> getUserProfile(
            final Authentication authentication) {

        final var user =
                userService.getUserByUsername(authentication.getName());

        return ResponseEntity.ok(userMapper.toUserProfileDto(user));
    }

    @GetMapping()
    public ResponseEntity<Page<UserProfileDto>> getAllUsers(
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
}