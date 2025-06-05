package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.CartItemDTO.CartItemResponseDto;
import com.petros.bookstore.model.CartItem;

public final class CartItemMapper {

    public static CartItemResponseDto toDto(CartItem item) {
        return new CartItemResponseDto(item.getId(), item.getBook(), item.getQuantity());
    }
}
