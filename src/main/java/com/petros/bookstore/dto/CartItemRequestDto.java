package com.petros.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemRequestDto(

        @NotNull(message = "bookId is mandatory")
        Long bookId,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity

) {}
