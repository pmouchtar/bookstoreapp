package com.petros.bookstore.dto.OrderDTO;

import com.petros.bookstore.model.enums.Status;
import java.sql.Timestamp;
import java.util.List;

public record OrderResponseDto(Long id, Long userId, Status status, Double totalPrice, Timestamp orderDate,
        List<OrderItemResponseDto> items) {
}