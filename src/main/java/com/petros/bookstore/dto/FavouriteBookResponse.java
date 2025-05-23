package com.petros.bookstore.dto;

import java.time.Instant;

public record FavouriteBookResponse(Long id, Long bookId, Instant addedAt) {}
