package com.petros.bookstore.dto;

import com.petros.bookstore.model.enums.Status;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequestDto(
        @NotNull Status status) {}