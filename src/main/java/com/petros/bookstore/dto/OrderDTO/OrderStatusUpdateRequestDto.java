package com.petros.bookstore.dto.OrderDTO;

import com.petros.bookstore.model.enums.Status;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequestDto(@NotNull Status status) {
}