package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.orderdto.OrderItemResponseDto;
import com.petros.bookstore.model.OrderItem;

public final class OrderItemMapper {
    public static OrderItemResponseDto toDto(OrderItem item) {
        Double price = item.getBook().getPrice();
        String subTotal = String.format("%.2f", price * item.getQuantity());
        return new OrderItemResponseDto(item.getId(), item.getBook().getId(), //
                item.getBook().getTitle(), price, item.getQuantity(), subTotal);
    }
}