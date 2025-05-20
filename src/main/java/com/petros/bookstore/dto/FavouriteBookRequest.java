package com.petros.bookstore.dto;

import jakarta.validation.constraints.NotNull;

public record FavouriteBookRequest(
        @NotNull(message = "bookId is mandatory")
        Long bookId) {
}