package com.petros.bookstore.controller;

import com.petros.bookstore.dto.FavouriteBookRequestDto;
import com.petros.bookstore.dto.FavouriteBookResponseDto;
import com.petros.bookstore.service.FavouriteBookService;
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
@RequestMapping("/users/me")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class FavouriteBookUserController {

  @Autowired private FavouriteBookService favouriteService;

  private Long extractUserId(Authentication auth) {
    return ((Jwt) auth.getPrincipal()).getClaim("userId");
  }

  @PostMapping("/favourite-books")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<FavouriteBookResponseDto> addFavouriteBook(
      Authentication auth, @Valid @RequestBody FavouriteBookRequestDto request) {

    Long userId = extractUserId(auth);
    FavouriteBookResponseDto response = favouriteService.addToFavourites(userId, request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/favourite-books")
  @SecurityRequirement(name = "bearerAuth")
  public Page<FavouriteBookResponseDto> getMyFavouriteBooks(Authentication auth, Pageable pageable) {

    Long userId = extractUserId(auth);
    return favouriteService.getFavourites(userId, pageable);
  }

  @DeleteMapping("/favourite-books/{bookId}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Void> deleteFavouriteBook(Authentication auth, @PathVariable Long bookId) {

    Long userId = extractUserId(auth);
    favouriteService.removeFromFavourites(userId, bookId);
    return ResponseEntity.noContent().build();
  }
}
