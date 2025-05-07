package com.petros.bookstore.dto;

public record RegistrationRequestDto(
        String username,
        String password,
        String firstName,
        String lastName
) {
}
