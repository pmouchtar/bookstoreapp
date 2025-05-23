package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.OrderItemResponse;
import com.petros.bookstore.dto.OrderResponse;
import com.petros.bookstore.model.Order;
import java.util.List;

public class OrderMapper {
    public static OrderResponse toDto(Order order) {
        List<OrderItemResponse> items =
                order.getOrderItems().stream().map(OrderItemMapper::toDto).toList();
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getTotal_price(),
                order.getOrder_date(),
                items);
    }
}