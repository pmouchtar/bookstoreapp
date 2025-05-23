package com.petros.bookstore.dto;

import com.petros.bookstore.model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
  private Long id;
  private Book book;
  private int quantity;
}
