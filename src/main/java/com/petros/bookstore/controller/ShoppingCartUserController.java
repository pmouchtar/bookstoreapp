package com.petros.bookstore.controller;

import com.petros.bookstore.dto.cartitemdto.CartItemRequestDto;
import com.petros.bookstore.dto.cartitemdto.CartItemResponseDto;
import com.petros.bookstore.dto.cartitemdto.CartItemUpdateRequestDto;
import com.petros.bookstore.service.ShoppingCartService;
import com.petros.bookstore.utils.AuthUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing shopping cart items for the currently authenticated
 * user. Provides endpoints to add, retrieve, update, and delete cart items.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/shopping-cart/items")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class ShoppingCartUserController {

    @Autowired
    ShoppingCartService shoppingCartService;

    private final AuthUtils authUtils;

    private Long userId;

    /**
     * Adds an item to the current user's shopping cart.
     *
     * @param request
     *            the item to add
     * @return the added CartItemResponseDto
     */
    @PostMapping()
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CartItemResponseDto> addItemToCart(//
            @Valid @RequestBody CartItemRequestDto request) {
        userId = authUtils.extractUserId();
        CartItemResponseDto response = shoppingCartService.addToCart(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves paginated cart items for the current user.
     *
     * @param pageable
     *            pagination and sorting information
     * @return a page of CartItemResponseDto objects
     */
    @GetMapping()
    @SecurityRequirement(name = "bearerAuth")
    public Page<CartItemResponseDto> getMyCartItems(Pageable pageable) {
        userId = authUtils.extractUserId();
        return shoppingCartService.getCartItems(userId, pageable);
    }

    /**
     * Retrieves a specific cart item by its ID for the current user.
     *
     * @param itemId
     *            the ID of the item
     * @return the CartItemResponseDto
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<CartItemResponseDto> getBook(@PathVariable Long itemId) {
        userId = authUtils.extractUserId();
        CartItemResponseDto response = shoppingCartService.findItemById(itemId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates a specific item in the current user's cart.
     *
     * @param itemId
     *            the ID of the item
     * @param request
     *            the updated item details
     * @return the updated CartItemResponseDto
     */
    @PutMapping("/{itemId}")
    public ResponseEntity<CartItemResponseDto> updateCartItem(@PathVariable Long itemId,
            @Valid @RequestBody CartItemUpdateRequestDto request) {
        userId = authUtils.extractUserId();
        CartItemResponseDto response = shoppingCartService.updateCartItem(itemId, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a specific item from the current user's cart.
     *
     * @param itemId
     *            the ID of the item
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{itemId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long itemId) {
        userId = authUtils.extractUserId();
        shoppingCartService.removeFromCart(userId, itemId);
        return ResponseEntity.noContent().build();
    }
}
