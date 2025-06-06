package com.petros.bookstore.dto.cartitemdto;

import jakarta.validation.constraints.Min;

public record CartItemUpdateRequestDto(

        @Min(value = 0, message = "Quantity must be at least 0") int quantity

) {
}