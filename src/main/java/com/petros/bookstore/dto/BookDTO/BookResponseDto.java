package com.petros.bookstore.dto.BookDTO;

import com.petros.bookstore.model.enums.Genre;

public record BookResponseDto(Long id, String title, String author, String description, Double price, int availability,
        Genre genre) {
}
