package com.petros.bookstore.controller;

import com.petros.bookstore.dto.OrderResponseDto;
import com.petros.bookstore.service.OrderService;
import com.petros.bookstore.utils.AuthUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/orders")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class OrderUserController {

    private final OrderService orderService;

    private final AuthUtils authUtils = new AuthUtils();

    private Long userId;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponseDto> placeOrder(Pageable pageable) throws BadRequestException {

        userId = authUtils.extractUserId();
        OrderResponseDto response = orderService.placeOrder(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public Page<OrderResponseDto> myOrders(Pageable pageable) {

        userId = authUtils.extractUserId();
        return orderService.getOrdersForUser(userId, pageable);
    }

    @GetMapping("/{orderId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponseDto> myOrder(@PathVariable Long orderId) {

        userId = authUtils.extractUserId();
        OrderResponseDto response =orderService.getOrderForUser(orderId, userId);
        return ResponseEntity.ok(response);
    }
}