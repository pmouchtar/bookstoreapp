package com.petros.bookstore.dto.CartItemDTO;

import com.petros.bookstore.model.Book;

public record CartItemResponseDto(Long id, Book book, int quantity) {
}