package com.petros.bookstore.dto;

import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.enums.Genre;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    @Min(value = 0, message = "Price must be >= 0")
    private Float price;

    @Min(value = 0, message = "Availability must be >= 0")
    private Integer availability;

    @NotNull(message = "Genre is required")
    @NotBlank(message = "Genre cannot be empty")
    private Genre genre;
}
