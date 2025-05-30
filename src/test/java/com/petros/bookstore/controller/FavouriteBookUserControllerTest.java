package com.petros.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.TestDummyJwtFilter;
import com.petros.bookstore.config.TestSecurityConfig;
import com.petros.bookstore.config.TestServiceConfig;
import com.petros.bookstore.dto.FavouriteBookDTO.FavouriteBookRequestDto;
import com.petros.bookstore.dto.FavouriteBookDTO.FavouriteBookResponseDto;
import com.petros.bookstore.exception.customException.ResourceAlreadyExistsException;
import com.petros.bookstore.service.FavouriteBookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.petros.bookstore.exception.customException.ResourceNotFoundException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.containsString;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavouriteBookUserController.class)
@Import({TestServiceConfig.class, TestSecurityConfig.class, TestDummyJwtFilter.class})
@ActiveProfiles("test")
class FavouriteBookUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FavouriteBookService favouriteService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/users/me/favourite-books";
    private static final String HEADER = "X-USER-ID";

    @Test
    void testAddFavouriteBook() throws Exception {
        FavouriteBookRequestDto request = new FavouriteBookRequestDto(42L);
        FavouriteBookResponseDto response = new FavouriteBookResponseDto(1L, 42L, Instant.now());

        Mockito.when(favouriteService.addToFavourites(eq(123L), any())).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, "123")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.bookId").value(response.bookId()));
    }

    @Test
    void testGetMyFavouriteBooks() throws Exception {
        FavouriteBookResponseDto response = new FavouriteBookResponseDto(1L, 42L, Instant.now());

        Mockito.when(favouriteService.getFavourites(eq(123L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1));

        mockMvc.perform(get(BASE_URL)
                        .header(HEADER, "123")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response.id()))
                .andExpect(jsonPath("$.content[0].bookId").value(response.bookId()));
    }

    @Test
    void testDeleteFavouriteBook() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/42")
                        .header(HEADER, "123"))
                .andExpect(status().isNoContent());

        Mockito.verify(favouriteService).removeFromFavourites(123L, 42L);
    }

    @Test
    void testAddFavouriteBook_withInvalidRequest_returnsBadRequest() throws Exception {
        // Missing bookId
        String invalidRequestJson = "{}";

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, "123")
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddFavouriteBook_whenBookAlreadyInFavourites_returnsConflict() throws Exception {
        FavouriteBookRequestDto request = new FavouriteBookRequestDto(42L);

        Mockito.when(favouriteService.addToFavourites(eq(123L), any()))
                .thenThrow(new ResourceAlreadyExistsException("Book already in favourites"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, "123")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("already in favourites")));
    }

    @Test
    void testDeleteFavouriteBook_whenNotFound_returnsNotFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Favourite not found"))
                .when(favouriteService).removeFromFavourites(123L, 99L);

        mockMvc.perform(delete(BASE_URL + "/99")
                        .header(HEADER, "123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    void testGetMyFavouriteBooks_whenUserNotFound_returnsNotFound() throws Exception {
        Mockito.when(favouriteService.getFavourites(eq(123L), any(Pageable.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get(BASE_URL)
                        .header(HEADER, "123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("User not found")));
    }
}
