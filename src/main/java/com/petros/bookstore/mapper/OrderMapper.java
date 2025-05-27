package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.OrderItemResponseDto;
import com.petros.bookstore.dto.OrderResponseDto;
import com.petros.bookstore.model.Order;
import java.util.List;

public class OrderMapper {
    public static OrderResponseDto toDto(Order order) {
        List<OrderItemResponseDto> items =
                order.getOrderItems().stream().map(OrderItemMapper::toDto).toList();
        return new OrderResponseDto(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getTotal_price(),
                order.getOrder_date(),
                items);
    }
}