package com.petros.bookstore.dto;

import com.petros.bookstore.model.enums.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
  private Long id;
  private String title;
  private String author;
  private String description;
  private Double price;
  private int availability;
  private Genre genre;
}
