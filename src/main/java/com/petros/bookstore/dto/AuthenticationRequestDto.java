package com.petros.bookstore.dto;

public record AuthenticationRequestDto(
        String username,
        String password
) {
}
