package com.petros.bookstore.dto.favouritebookdto;

import jakarta.validation.constraints.NotNull;

public record FavouriteBookRequestDto(@NotNull(message = "bookId is mandatory") Long bookId) {
}
