package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.CartItemResponseDto;
import com.petros.bookstore.model.Cart_Item;

public final class CartItemMapper {

  public static CartItemResponseDto toDto(Cart_Item item) {
    return new CartItemResponseDto(
            item.getId(),
            item.getBook(),
            item.getQuantity());
  }
}
