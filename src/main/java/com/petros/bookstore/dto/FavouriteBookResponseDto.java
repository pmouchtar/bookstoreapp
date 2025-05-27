package com.petros.bookstore.dto;

import java.time.Instant;

public record FavouriteBookResponseDto(Long id, Long bookId, Instant addedAt) {}
