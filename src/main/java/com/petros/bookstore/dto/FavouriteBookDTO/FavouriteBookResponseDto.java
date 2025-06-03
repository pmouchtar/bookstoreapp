package com.petros.bookstore.dto.FavouriteBookDTO;

import java.time.Instant;

public record FavouriteBookResponseDto(Long id, Long bookId, Instant addedAt) {
}
