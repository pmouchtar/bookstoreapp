package com.petros.bookstore.controller;

import com.petros.bookstore.dto.FavouriteBookDTO.FavouriteBookRequestDto;
import com.petros.bookstore.dto.FavouriteBookDTO.FavouriteBookResponseDto;
import com.petros.bookstore.service.FavouriteBookService;
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

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class FavouriteBookUserController {

    private final AuthUtils authUtils = new AuthUtils();

    private Long userId;

    @Autowired
    private FavouriteBookService favouriteService;

    @PostMapping("/favourite-books")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<FavouriteBookResponseDto> addFavouriteBook(
            @Valid @RequestBody FavouriteBookRequestDto request) {

        userId = authUtils.extractUserId();
        FavouriteBookResponseDto response = favouriteService.addToFavourites(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/favourite-books")
    @SecurityRequirement(name = "bearerAuth")
    public Page<FavouriteBookResponseDto> getMyFavouriteBooks(Pageable pageable) {

        userId = authUtils.extractUserId();
        return favouriteService.getFavourites(userId, pageable);
    }

    @DeleteMapping("/favourite-books/{bookId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteFavouriteBook(@PathVariable Long bookId) {

        userId = authUtils.extractUserId();
        favouriteService.removeFromFavourites(userId, bookId);
        return ResponseEntity.noContent().build();
    }
}
