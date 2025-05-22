package com.petros.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CartItemRequest {

    @NotNull(message = "bookId is mandatory")
    Long bookId;

    @Min(value = 1, message = "Quantity must be at least 0")
    private int quantity;
}
