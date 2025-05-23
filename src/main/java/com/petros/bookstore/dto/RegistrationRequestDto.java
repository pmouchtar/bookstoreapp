package com.petros.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrationRequestDto(
    @NotBlank(message = "Username is required")
        @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
        @Pattern(
            regexp = "^(?!\\d+$)[A-Za-z\\d._-]+$",
            message =
                "Username cannot be only numbers and may contain letters, digits, dots, underscores, or hyphens")
        String username,
    @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
        @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$",
            message =
                "Password must contain at least one uppercase letter, one lowercase letter, and one number")
        String password,
    @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must be at most 50 characters")
        @Pattern(
            regexp = "^[A-Za-zΑ-Ωα-ωΆ-Ώά-ώ]+$",
            message = "First name must contain only letters (no numbers or symbols)")
        String firstName,
    @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must be at most 50 characters")
        @Pattern(
            regexp = "^[A-Za-zΑ-Ωα-ωΆ-Ώά-ώ]+$",
            message = "Last name must contain only letters (no numbers or symbols)")
        String lastName) {}
