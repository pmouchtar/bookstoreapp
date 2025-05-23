package com.petros.bookstore.controller;

import com.petros.bookstore.dto.OrderResponse;
import com.petros.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/orders")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class OrderUserController {

    private final OrderService orderService;

    private Long extractUserId(Authentication auth) {
        return ((Jwt) auth.getPrincipal()).getClaim("userId");
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public OrderResponse placeOrder(Authentication auth, Pageable pageable) throws BadRequestException {
        return orderService.placeOrder(extractUserId(auth), pageable);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public Page<OrderResponse> myOrders(Authentication auth, Pageable pageable) {
        return orderService.getOrdersForUser(extractUserId(auth), pageable);
    }

    @GetMapping("/{orderId}")
    @SecurityRequirement(name = "bearerAuth")
    public OrderResponse myOrder(Authentication auth, @PathVariable Long orderId) {
        return orderService.getOrderForUser(orderId, extractUserId(auth));
    }
}