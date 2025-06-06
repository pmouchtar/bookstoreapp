package com.petros.bookstore.dto.bookdto;

import com.petros.bookstore.enums.Genre;

public record BookResponseDto(//
        Long id, //
        String title, //
        String author, //
        String description, //
        Double price, //
        int availability, //
        Genre genre) {
}
