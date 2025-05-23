package com.petros.bookstore.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CartItemUpdateRequest {

  @Min(value = 0, message = "Quantity must be at least 0")
  private int quantity;
}
