package com.petros.bookstore.dto.orderdto;

import com.petros.bookstore.enums.Status;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequestDto(@NotNull Status status) {
}