package com.petros.bookstore.controller;

import com.petros.bookstore.dto.OrderDTO.OrderResponseDto;
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

/**
 * REST controller for users to manage their own orders.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/orders")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class OrderUserController {

    private final OrderService orderService;
    private final AuthUtils authUtils;

    private Long userId;

    /**
     * Places a new order for the authenticated user.
     *
     * @param pageable
     *            pagination info for handling cart items internally (if applicable)
     * @return the placed order as OrderResponseDto
     * @throws BadRequestException
     *             if placing the order fails (e.g. empty cart)
     */
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponseDto> placeOrder(//
            Pageable pageable) throws BadRequestException {

        userId = authUtils.extractUserId();
        OrderResponseDto response = orderService.placeOrder(userId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves paginated orders of the authenticated user.
     *
     * @param pageable
     *            pagination and sorting information
     * @return a page of OrderResponseDto for the user's orders
     */
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public Page<OrderResponseDto> myOrders(Pageable pageable) {

        userId = authUtils.extractUserId();
        return orderService.getOrdersForUser(userId, pageable);
    }

    /**
     * Retrieves a specific order belonging to the authenticated user.
     *
     * @param orderId
     *            the ID of the order to retrieve
     * @return the OrderResponseDto if the order belongs to the user
     */
    @GetMapping("/{orderId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponseDto> myOrder(@PathVariable Long orderId) {

        userId = authUtils.extractUserId();
        OrderResponseDto response = orderService.getOrderForUser(orderId, userId);
        return ResponseEntity.ok(response);
    }
}
