package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.BookRequest;
import com.petros.bookstore.dto.BookResponse;
import com.petros.bookstore.model.Book;

public class BookMapper {

    public static Book toEntity(BookRequest request) {
        return new Book(
                request.getTitle(),
                request.getAuthor(),
                request.getDescription(),
                request.getPrice(),
                request.getAvailability(),
                request.getGenre()
        );
    }

    public static BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getPrice(),
                book.getAvailability(),
                book.getGenre()
        );
    }
}
