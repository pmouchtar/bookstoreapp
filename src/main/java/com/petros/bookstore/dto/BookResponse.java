package com.petros.bookstore.dto;

import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.enums.Genre;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String description;
    private float price;
    private int availability;
    private Genre genre;

    public static BookResponse fromEntity(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setDescription(book.getDescription());
        response.setPrice(book.getPrice());
        response.setAvailability(book.getAvailability());
        response.setGenre(book.getGenre());
        return response;
    }
}
//Genre.valueOf(book.getGenre().name())