package com.petros.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.TestDummyJwtFilter;
import com.petros.bookstore.config.TestSecurityConfig;
import com.petros.bookstore.config.TestServiceConfig;
import com.petros.bookstore.dto.FavouriteBookDTO.FavouriteBookResponseDto;
import com.petros.bookstore.service.FavouriteBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavouriteBookAdminController.class)
@Import({TestServiceConfig.class, TestSecurityConfig.class, TestDummyJwtFilter.class})
@ActiveProfiles("test")
class FavouriteBookAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FavouriteBookService favouriteService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ADMIN_HEADER = "X-USER-ID";
    private static final String BASE_URL = "/users/1/favourite-books";


    @Test
    void testGetUserFavouriteBooks_success() throws Exception {
        List<FavouriteBookResponseDto> list = List.of(new FavouriteBookResponseDto(1L, 101L, Instant.now()),
                new FavouriteBookResponseDto(2L, 102L, Instant.now()));
        Page<FavouriteBookResponseDto> page = new PageImpl<>(list);

        when(favouriteService.getFavourites(eq(1L), Mockito.any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get(BASE_URL).header(ADMIN_HEADER, "999") // simulate admin
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].bookId").value(101));
    }

    @Test
    void testGetUserFavouriteBooks_userNotFound_returnsNotFound() throws Exception {
        when(favouriteService.getFavourites(eq(404L), Mockito.any(Pageable.class))).thenThrow(
                new com.petros.bookstore.exception.customException.ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/users/404/favourite-books").header(ADMIN_HEADER, "999")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}