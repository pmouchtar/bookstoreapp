package com.petros.bookstore.dto;

import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.enums.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookRequest {

    private String title;
    private String author;
    private String description;
    private float price;
    private int availability;
    private Genre genre;
}
