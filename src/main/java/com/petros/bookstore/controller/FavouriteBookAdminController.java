package com.petros.bookstore.controller;

import com.petros.bookstore.dto.FavouriteBookDTO.FavouriteBookResponseDto;
import com.petros.bookstore.service.FavouriteBookService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for admin to manage users' favourite books.
 */
@Validated
@RestController
@RequestMapping("/users/{userId}/favourite-books")
@PreAuthorize("hasAnyRole('ADMIN')")
public class FavouriteBookAdminController {
    @Autowired
    private FavouriteBookService favouriteService;
    /**
     * Retrieves a paginated list of favourite books for a specific user.
     *
     * @param userId
     *            the ID of the user
     * @param pageable
     *            pagination and sorting information
     * @return a page of FavouriteBookResponseDto objects
     */

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping()
    public Page<FavouriteBookResponseDto> getUserFavouriteBooks(//
            @PathVariable Long userId, Pageable pageable) {
        return favouriteService.getFavourites(userId, pageable);
    }
}
