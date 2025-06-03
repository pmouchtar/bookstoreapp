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

/**
 * REST controller for administrators to manage orders.
 */
@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class OrderAdminController {

    private final OrderService orderService;

    /**
     * Retrieves all orders with pagination.
     *
     * @param pageable pagination and sorting information
     * @return a page of OrderResponseDto representing all orders
     */
    @GetMapping("/orders")
    @SecurityRequirement(name = "bearerAuth")
    public Page<OrderResponseDto> allOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    /**
     * Updates the status of a specific order.
     *
     * @param orderId the ID of the order to update
     * @param request the request DTO containing the new status
     * @return the updated order response DTO
     */
    @PutMapping("/orders/{orderId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponseDto> updateStatus(@PathVariable Long orderId,
                                                         @Valid @RequestBody OrderStatusUpdateRequestDto request) {

        OrderResponseDto response = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a specific order by its ID.
     *
     * @param orderId the ID of the order to retrieve
     * @return the order response DTO
     */
    @GetMapping("/orders/{orderId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long orderId) {

        OrderResponseDto response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves paginated orders for a specific user.
     *
     * @param userId   the ID of the user whose orders are requested
     * @param pageable pagination and sorting information
     * @return a page of OrderResponseDto objects for the user
     */
    @GetMapping("/users/{userId}/orders")
    @SecurityRequirement(name = "bearerAuth")
    public Page<OrderResponseDto> userOrders(@PathVariable Long userId, Pageable pageable) {
        return orderService.getOrdersForUser(userId, pageable);
    }
}
