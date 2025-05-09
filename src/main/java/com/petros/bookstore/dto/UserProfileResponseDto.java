package com.petros.bookstore.dto;

public record UserProfileResponseDto(
        Long id,
        String firstName,
        String lastName,
        String username,
        String role) {
}
