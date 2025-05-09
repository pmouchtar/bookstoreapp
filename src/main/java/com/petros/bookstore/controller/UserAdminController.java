package com.petros.bookstore.controller;

import com.petros.bookstore.dto.UserProfileResponseDto;
import com.petros.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
