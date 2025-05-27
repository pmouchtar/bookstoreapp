package com.petros.bookstore.dto;

import jakarta.validation.constraints.NotNull;

public record FavouriteBookRequestDto(@NotNull(message = "bookId is mandatory") Long bookId) {}
