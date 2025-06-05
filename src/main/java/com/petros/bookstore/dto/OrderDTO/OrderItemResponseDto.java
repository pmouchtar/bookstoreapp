package com.petros.bookstore.dto.OrderDTO;

public record OrderItemResponseDto(//
        Long id, Long bookId, String title, Double price, int quantity, String subTotal) {
}