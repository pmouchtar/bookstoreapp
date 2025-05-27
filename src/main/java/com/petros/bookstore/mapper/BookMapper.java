package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.BookRequestDto;
import com.petros.bookstore.dto.BookResponseDto;
import com.petros.bookstore.model.Book;

public class BookMapper {

  public static Book toEntity(BookRequestDto request) {
    return new Book(
        request.getTitle(),
        request.getAuthor(),
        request.getDescription(),
        request.getPrice(),
        request.getAvailability(),
        request.getGenre());
  }

  public static BookResponseDto toResponse(Book book) {
    return new BookResponseDto(
        book.getId(),
        book.getTitle(),
        book.getAuthor(),
        book.getDescription(),
        book.getPrice(),
        book.getAvailability(),
        book.getGenre());
  }
}
