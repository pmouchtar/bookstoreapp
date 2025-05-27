package com.petros.bookstore.controller;

import com.petros.bookstore.dto.*;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/shopping-cart/items")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class ShoppingCartUserController {

  @Autowired ShoppingCartService shoppingCartService;

  private final AuthUtils authUtils = new AuthUtils();

  private Long userId;

  @PostMapping()
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<CartItemResponseDto> addItemToCart(
      @Valid @RequestBody CartItemRequestDto request) {

    userId = authUtils.extractUserId();
    CartItemResponseDto response = shoppingCartService.addToCart(userId, request);
    return ResponseEntity.ok(response);
  }

  @GetMapping()
  @SecurityRequirement(name = "bearerAuth")
  public Page<CartItemResponseDto> getMyCartItems(Pageable pageable) {

    userId = authUtils.extractUserId();
    return shoppingCartService.getCartItems(userId, pageable);
  }

  @GetMapping("/{itemId}")
  public ResponseEntity<CartItemResponseDto> getBook(@PathVariable Long itemId) {

    userId = authUtils.extractUserId();
    CartItemResponseDto response = shoppingCartService.findItemById(itemId, userId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{itemId}")
  public ResponseEntity<CartItemResponseDto> updateCartItem(
      @PathVariable Long itemId,
      @Valid @RequestBody CartItemUpdateRequestDto request) {

    userId = authUtils.extractUserId();
    CartItemResponseDto response = shoppingCartService.updateCartItem(itemId, request, userId);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{itemId}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Void> deleteCartItem(@PathVariable Long itemId) {

    userId = authUtils.extractUserId();
    shoppingCartService.removeFromCart(userId, itemId);
    return ResponseEntity.noContent().build();
  }
}
