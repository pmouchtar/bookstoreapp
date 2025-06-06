package com.petros.bookstore.dto.orderdto;

public record OrderItemResponseDto(//
        Long id, Long bookId, String title, Double price, int quantity, String subTotal) {
}