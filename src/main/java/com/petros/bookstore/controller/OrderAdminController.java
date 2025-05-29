package com.petros.bookstore.controller;

import com.petros.bookstore.dto.OrderDTO.OrderResponseDto;
import com.petros.bookstore.dto.OrderDTO.OrderStatusUpdateRequestDto;
import com.petros.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class OrderAdminController {

    private final OrderService orderService;

    @GetMapping("/orders")
    @SecurityRequirement(name = "bearerAuth")
    public Page<OrderResponseDto> allOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @PutMapping("/orders/{orderId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponseDto> updateStatus(@PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateRequestDto request) {

        OrderResponseDto response = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{orderId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long orderId) {

        OrderResponseDto response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/orders")
    @SecurityRequirement(name = "bearerAuth")
    public Page<OrderResponseDto> userOrders(@PathVariable Long userId, Pageable pageable) {
        return orderService.getOrdersForUser(userId, pageable);
    }

}