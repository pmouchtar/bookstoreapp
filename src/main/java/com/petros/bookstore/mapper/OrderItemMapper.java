package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.OrderItemResponse;
import com.petros.bookstore.model.Order_Item;

public class OrderItemMapper {
    public static OrderItemResponse toDto(Order_Item item) {
        Double price = item.getBook().getPrice();
        return new OrderItemResponse(
                item.getId(),
                item.getBook().getId(),
                item.getBook().getTitle(),
                price,
                item.getQuantity(),
                price * item.getQuantity());
    }
}