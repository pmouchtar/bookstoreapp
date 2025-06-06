package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.bookdto.BookRequestDto;
import com.petros.bookstore.dto.bookdto.BookResponseDto;
import com.petros.bookstore.model.Book;

public final class BookMapper {

    public static Book toEntity(BookRequestDto request) {
        return new Book(request.title(), request.author(), request.description(), request.price(),
                request.availability(), request.genre());
    }

    public static BookResponseDto toResponse(Book book) {
        return new BookResponseDto(book.getId(), book.getTitle(), //
                book.getAuthor(), book.getDescription(), //
                book.getPrice(), book.getAvailability(), book.getGenre());
    }
}
