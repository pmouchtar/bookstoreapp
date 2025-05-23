package com.petros.bookstore.dto;

public record OrderItemResponse(
        Long id,
        Long bookId,
        String title,
        Double price,
        int quantity,
        Double subTotal) {}