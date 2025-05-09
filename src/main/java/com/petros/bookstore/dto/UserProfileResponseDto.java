package com.petros.bookstore.dto;

public record UserProfileResponseDto(
        String firstName,
        String lastName,
        String username) {
}
