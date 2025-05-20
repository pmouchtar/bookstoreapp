package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.CartItemResponse;
import com.petros.bookstore.model.Cart_Item;

public final class CartItemMapper {

    private CartItemMapper() {
    }

    public static CartItemResponse toDto(Cart_Item item) {
        if (item == null) {
            return null;
        }
        CartItemResponse dto = new CartItemResponse();
        dto.setId(item.getId());
        dto.setBook(item.getBook());
        dto.setQuantity(item.getQuantity());
        return dto;
    }
}