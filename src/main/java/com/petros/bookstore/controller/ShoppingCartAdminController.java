package com.petros.bookstore.controller;

import com.petros.bookstore.dto.CartItemDTO.CartItemResponseDto;
import com.petros.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/users/{userId}/shopping-cart/items")
@PreAuthorize("hasRole('ADMIN')")
public class ShoppingCartAdminController {

    @Autowired
    ShoppingCartService shoppingCartService;

    @GetMapping()
    @SecurityRequirement(name = "bearerAuth")
    public Page<CartItemResponseDto> getMyCartItems(Authentication auth, @PathVariable Long userId, Pageable pageable) {

        return shoppingCartService.getCartItems(userId, pageable);
    }

    @GetMapping("{itemId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CartItemResponseDto> getBook(Authentication auth, @PathVariable Long userId,
            @PathVariable Long itemId) {

        CartItemResponseDto response = shoppingCartService.findItemById(itemId, userId);
        return ResponseEntity.ok(response);
    }
}
