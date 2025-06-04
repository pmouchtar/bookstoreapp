package com.petros.bookstore.dto.RegistrationDTO;

import com.petros.bookstore.enums.Role;

public record RegistrationResponseDto(String username, String firstName, String lastName, Role role) {
}
