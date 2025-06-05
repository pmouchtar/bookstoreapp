package com.petros.bookstore.controller;

import com.petros.bookstore.dto.CartItemDTO.CartItemResponseDto;
import com.petros.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for admins to access shopping cart items of any user.
 * Provides functionality for viewing cart contents and specific cart items by
 * user.
 */
@Validated
@RestController
@RequestMapping("/users/{userId}/shopping-cart/items")
@PreAuthorize("hasRole('ADMIN')")
public class ShoppingCartAdminController {

    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * Retrieves paginated cart items of a specific user.
     *
     * @param userId
     *            the ID of the user whose cart is being accessed
     * @param pageable
     *            pagination and sorting information
     * @return a page of CartItemResponseDto representing the user's cart contents
     */
    @GetMapping()
    @SecurityRequirement(name = "bearerAuth")
    public Page<CartItemResponseDto> getMyCartItems(@PathVariable Long userId, Pageable pageable) {

        return shoppingCartService.getCartItems(userId, pageable);
    }

    /**
     * Retrieves a specific cart item for a given user.
     *
     * @param userId
     *            the ID of the user
     * @param itemId
     *            the ID of the cart item to retrieve
     * @return a ResponseEntity containing the CartItemResponseDto
     */
    @GetMapping("{itemId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CartItemResponseDto> getBook(//
            @PathVariable Long userId, @PathVariable Long itemId) {

        CartItemResponseDto response = shoppingCartService.findItemById(itemId, userId);
        return ResponseEntity.ok(response);
    }
}
