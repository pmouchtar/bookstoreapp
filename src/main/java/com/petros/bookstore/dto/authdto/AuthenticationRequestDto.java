package com.petros.bookstore.dto.authdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequestDto(//
        @NotBlank(message = "Username is required") //
        @Size(min = 4, max = 30, //
                message = "Username must be between 4 and 30 characters") //
        String username, //
        @NotBlank(message = "Password is required") //
        @Size(min = 8, max = 100, //
                message = "Password must be between 8 and 100 characters") //
        String password) {
}
