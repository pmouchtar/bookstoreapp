package com.petros.bookstore.dto.userdto;

import com.petros.bookstore.enums.Role;

public record UserProfileResponseDto(//
        Long id, String firstName, String lastName, String username, Role role) {
}
