package com.petros.bookstore.dto;

import com.petros.bookstore.model.enums.Genre;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateRequest {

    @Size(min = 1, message = "Title cannot be empty")
    private String title;

    @Size(min = 1, message = "Author cannot be empty")
    private String author;

    @Size(min = 1, message = "Description cannot be empty")
    private String description;

    @Min(value = 0, message = "Price must be at least 0")
    private Float price;

    @Min(value = 0, message = "Availability must be at least 0")
    private Integer availability;

    private Genre genre;
}
