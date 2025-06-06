package com.petros.bookstore.dto.favouritebookdto;

import java.time.Instant;

public record FavouriteBookResponseDto(Long id, Long bookId, Instant addedAt) {
}
