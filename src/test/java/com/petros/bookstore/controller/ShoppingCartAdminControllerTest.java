package com.petros.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.TestDummyJwtFilter;
import com.petros.bookstore.config.TestSecurityConfig;
import com.petros.bookstore.config.TestServiceConfig;
import com.petros.bookstore.dto.cartitemdto.CartItemResponseDto;
import com.petros.bookstore.exception.customException.ResourceNotFoundException;
import com.petros.bookstore.model.Book;
import com.petros.bookstore.enums.Genre;
import com.petros.bookstore.service.ShoppingCartService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShoppingCartAdminController.class)
@Import({TestServiceConfig.class, TestSecurityConfig.class, TestDummyJwtFilter.class})
@ActiveProfiles("test")
class ShoppingCartAdminControllerTest {

    private static final String HEADER = "X-USER-ID";
    private static final Long USER_ID = 10L;
    private final Book book = new Book("title", "author", "description", 45.99, 5, Genre.SCIENCE_FICTION);

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ShoppingCartService shoppingCartService;

    @Test
    void getUserCartItems_success() throws Exception {
        CartItemResponseDto item = new CartItemResponseDto(1L, book, 2);

        Mockito.when(shoppingCartService.getCartItems(eq(USER_ID), any()))
                .thenReturn(new PageImpl<>(List.of(item), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/users/{userId}/shopping-cart/items", USER_ID).param("page", "0").param("size", "10")
                .header(HEADER, 999)) // Simulates admin auth
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(item.id()))
                .andExpect(jsonPath("$.content[0].quantity").value(item.quantity()));
    }

    @Test
    void getUserCartItems_emptyPage_returnsEmptyList() throws Exception {
        Mockito.when(shoppingCartService.getCartItems(eq(USER_ID), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        mockMvc.perform(get("/users/{userId}/shopping-cart/items", USER_ID).header(HEADER, 999))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void getUserCartItemById_success() throws Exception {
        CartItemResponseDto item = new CartItemResponseDto(1L, book, 2);

        Mockito.when(shoppingCartService.findItemById(1L, USER_ID)).thenReturn(item);

        mockMvc.perform(get("/users/{userId}/shopping-cart/items/{itemId}", USER_ID, 1L).header(HEADER, 999))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(item.id()))
                .andExpect(jsonPath("$.quantity").value(item.quantity()));
    }

    @Test
    void getUserCartItemById_notFound() throws Exception {
        Mockito.when(shoppingCartService.findItemById(42L, USER_ID))
                .thenThrow(new ResourceNotFoundException("Item not found"));

        mockMvc.perform(get("/users/{userId}/shopping-cart/items/{itemId}", USER_ID, 42L).header(HEADER, 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Item not found")));
    }
}
