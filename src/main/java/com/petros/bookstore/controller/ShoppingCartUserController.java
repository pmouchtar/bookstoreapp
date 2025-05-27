package com.petros.bookstore.controller;

import com.petros.bookstore.dto.*;
import com.petros.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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
@RequestMapping("/users/me/shopping-cart/items")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class ShoppingCartUserController {

  @Autowired ShoppingCartService shoppingCartService;

  private Long extractUserId(Authentication auth) {
    return ((Jwt) auth.getPrincipal()).getClaim("userId");
  }

  @PostMapping()
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<CartItemResponseDto> addItemToCart(
      Authentication auth, @Valid @RequestBody CartItemRequestDto request) {

    Long userId = extractUserId(auth);
    CartItemResponseDto response = shoppingCartService.addToCart(userId, request);
    return ResponseEntity.ok(response);
  }

  @GetMapping()
  @SecurityRequirement(name = "bearerAuth")
  public Page<CartItemResponseDto> getMyCartItems(Authentication auth, Pageable pageable) {

    Long userId = extractUserId(auth);
    return shoppingCartService.getCartItems(userId, pageable);
  }

  @GetMapping("/{itemId}")
  public ResponseEntity<CartItemResponseDto> getBook(Authentication auth, @PathVariable Long itemId) {

    Long userId = extractUserId(auth);
    CartItemResponseDto response = shoppingCartService.findItemById(itemId, userId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{itemId}")
  public ResponseEntity<CartItemResponseDto> updateCartItem(
      Authentication auth,
      @PathVariable Long itemId,
      @Valid @RequestBody CartItemUpdateRequestDto request) {

    Long userId = extractUserId(auth);
    CartItemResponseDto response = shoppingCartService.updateCartItem(itemId, request, userId);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{itemId}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Void> deleteCartItem(Authentication auth, @PathVariable Long itemId) {

    Long userId = extractUserId(auth);
    shoppingCartService.removeFromCart(userId, itemId);
    return ResponseEntity.noContent().build();
  }
}
