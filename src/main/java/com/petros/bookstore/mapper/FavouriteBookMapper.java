package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.FavouriteBookResponseDto;
import com.petros.bookstore.model.Favourite_Book;

public final class FavouriteBookMapper {

  private FavouriteBookMapper() {}

  public static FavouriteBookResponseDto toDto(Favourite_Book entity) {
    return new FavouriteBookResponseDto(
            entity.getId(),
            entity.getBook().getId(),
            entity.getCreatedAt());
  }
}
