package com.petros.bookstore.dto;

import com.petros.bookstore.model.enums.Genre;
import jakarta.validation.constraints.*;
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
  @Size(max = 255, message = "Title must be at most 255 characters")
  private String title;

  @NotBlank(message = "Author is required")
  @Size(max = 255, message = "Author must be at most 255 characters")
  @Pattern(regexp = "^[\\p{L} ]+$", message = "Author name must contain only letters and spaces")
  private String author;

  @Size(max = 500, message = "Description must be at most 500 characters")
  private String description;

  @Min(value = 0, message = "Price must be >= 0")
  @Digits(
      integer = 5,
      fraction = 2,
      message = "Integer part must be <=5 digits, Decimal part must be <=2 digits")
  private Double price;

  @Min(value = 0, message = "Availability must be >= 0")
  private Integer availability;

  @NotNull(message = "Genre is required")
  private Genre genre;
}
