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

/**
 * REST controller for authenticated users to manage their favourite books.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/favourite-books")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class FavouriteBookUserController {

    private final AuthUtils authUtils;

    private Long userId;

    @Autowired
    private FavouriteBookService favouriteService;

    /**
     * Adds a book to the authenticated user's favourites.
     *
     * @param request the favourite book request DTO containing book details
     * @return the favourite book response DTO with added favourite info
     */
    @PostMapping()
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<FavouriteBookResponseDto> addFavouriteBook(
            @Valid @RequestBody FavouriteBookRequestDto request) {
        userId = authUtils.extractUserId();
        FavouriteBookResponseDto response = favouriteService.addToFavourites(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a paginated list of the authenticated user's favourite books.
     *
     * @param pageable pagination and sorting information
     * @return a page of FavouriteBookResponseDto objects
     */
    @GetMapping()
    @SecurityRequirement(name = "bearerAuth")
    public Page<FavouriteBookResponseDto> getMyFavouriteBooks(Pageable pageable) {
        userId = authUtils.extractUserId();
        return favouriteService.getFavourites(userId, pageable);
    }

    /**
     * Deletes a book from the authenticated user's favourites by book ID.
     *
     * @param bookId the ID of the book to remove from favourites
     * @return a response entity with no content on successful deletion
     */
    @DeleteMapping("/{bookId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteFavouriteBook(@PathVariable Long bookId) {
        userId = authUtils.extractUserId();
        favouriteService.removeFromFavourites(userId, bookId);
        return ResponseEntity.noContent().build();
    }
}
