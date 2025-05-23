package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.FavouriteBookResponse;
import com.petros.bookstore.model.Favourite_Book;

public final class FavouriteBookMapper {

  private FavouriteBookMapper() {}

  public static FavouriteBookResponse toDto(Favourite_Book entity) {
    return new FavouriteBookResponse(
        entity.getId(), entity.getBook().getId(), entity.getCreatedAt());
  }
}
