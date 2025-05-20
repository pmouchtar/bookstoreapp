package com.petros.bookstore.dto;

import com.petros.bookstore.model.enums.Role;

public record UserProfileResponseDto(
        Long id,
        String firstName,
        String lastName,
        String username,
        Role role) {
}
