package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.OrderItemResponseDto;
import com.petros.bookstore.model.Order_Item;

public final class OrderItemMapper {
    public static OrderItemResponseDto toDto(Order_Item item) {
        Double price = item.getBook().getPrice();
        return new OrderItemResponseDto(
                item.getId(),
                item.getBook().getId(),
                item.getBook().getTitle(),
                price,
                item.getQuantity(),
                OrderItemMapper.formatted(price, item.getQuantity()));
    }

    public static String formatted(Double price, int quantity){
        return String.format("%.2f", price * quantity);
    }
}