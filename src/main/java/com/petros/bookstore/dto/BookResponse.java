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
    private Float price;
    private int availability;
    private Genre genre;
}