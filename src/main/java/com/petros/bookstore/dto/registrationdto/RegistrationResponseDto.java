package com.petros.bookstore.dto.registrationdto;

import com.petros.bookstore.enums.Role;

public record RegistrationResponseDto(//
        String username, String firstName, String lastName, Role role) {
}
