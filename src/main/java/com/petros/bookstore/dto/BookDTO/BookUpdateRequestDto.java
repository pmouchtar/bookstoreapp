package com.petros.bookstore.dto.BookDTO;

import com.petros.bookstore.enums.Genre;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BookUpdateRequestDto(

        @Size(min = 1, max = 255, message = "Title cannot be empty or more than 255 letters") String title,

        @Size(min = 1, max = 255, message = "Author cannot be empty or  more than 255 letters") @Pattern(regexp = "^[\\p{L} ]+$", message = "Author name must contain only letters and spaces") String author,

        @Size(min = 1, message = "Description cannot be empty") String description,

        @Min(value = 0, message = "Price must be at least 0") Double price,

        @Min(value = 0, message = "Availability must be at least 0") Integer availability,

        Genre genre

) {
}
