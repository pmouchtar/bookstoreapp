package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.FavouriteBookDTO.FavouriteBookResponseDto;
import com.petros.bookstore.model.FavouriteBook;

public final class FavouriteBookMapper {

    private FavouriteBookMapper() {
    }

    public static FavouriteBookResponseDto toDto(FavouriteBook entity) {
        return new FavouriteBookResponseDto(entity.getId(), //
                entity.getBook().getId(), entity.getCreatedAt());
    }
}
