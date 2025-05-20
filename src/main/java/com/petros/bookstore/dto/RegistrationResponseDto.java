package com.petros.bookstore.dto;

import com.petros.bookstore.model.enums.Role;

public record RegistrationResponseDto(
            String username,
            String firstName,
            String lastName,
            Role role
    ){
}
