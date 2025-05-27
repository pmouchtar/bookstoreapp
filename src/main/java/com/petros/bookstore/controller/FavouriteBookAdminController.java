package com.petros.bookstore.controller;

import com.petros.bookstore.dto.FavouriteBookResponseDto;
import com.petros.bookstore.service.FavouriteBookService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/users/{userId}/favourite-books")
@PreAuthorize("hasAnyRole('ADMIN')")
public class FavouriteBookAdminController {

  @Autowired private FavouriteBookService favouriteService;

  @SecurityRequirement(name = "bearerAuth")
  @GetMapping()
  public Page<FavouriteBookResponseDto> getUserFavouriteBooks(
      @PathVariable Long userId, Pageable pageable) {

    return favouriteService.getFavourites(userId, pageable);
  }
}
