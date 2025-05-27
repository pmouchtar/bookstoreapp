package com.petros.bookstore.controller;

import com.petros.bookstore.dto.OrderResponseDto;
import com.petros.bookstore.dto.OrderStatusUpdateRequestDto;
import com.petros.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class OrderAdminController {

    private final OrderService orderService;

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public Page<OrderResponseDto> allOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @GetMapping("/users/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    public Page<OrderResponseDto> userOrders(@PathVariable Long userId, Pageable pageable) {
        return orderService.getOrdersForUser(userId, pageable);
    }

    @PutMapping("/{orderId}/status")
    @SecurityRequirement(name = "bearerAuth")
    public OrderResponseDto updateStatus(
            @PathVariable Long orderId, @Valid @RequestBody OrderStatusUpdateRequestDto request) {
        return orderService.updateOrderStatus(orderId, request);
    }
}