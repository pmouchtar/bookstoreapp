package com.petros.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.TestDummyJwtFilter;
import com.petros.bookstore.config.TestSecurityConfig;
import com.petros.bookstore.config.TestServiceConfig;
import com.petros.bookstore.dto.CartItemDTO.CartItemRequestDto;
import com.petros.bookstore.dto.CartItemDTO.CartItemResponseDto;
import com.petros.bookstore.dto.CartItemDTO.CartItemUpdateRequestDto;
import com.petros.bookstore.exception.customException.ResourceNotFoundException;
import com.petros.bookstore.model.Book;
import com.petros.bookstore.enums.Genre;
import com.petros.bookstore.service.ShoppingCartService;
import com.petros.bookstore.utils.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShoppingCartUserController.class)
@Import({TestServiceConfig.class, TestSecurityConfig.class, TestDummyJwtFilter.class})
@ActiveProfiles("test")
class ShoppingCartUserControllerTest {

    private static final String HEADER = "X-USER-ID";
    private static final Long USER_ID = 123L;
    private static final String BASE_URL = "/users/me/shopping-cart/items";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    AuthUtils authUtils;

    @Autowired
    ShoppingCartService shoppingCartService; // mocked bean

    private final Book book = new Book("title", "author", "description", 45.99, 5, Genre.SCIENCE_FICTION);

    @BeforeEach
    void setup() {
        Mockito.when(authUtils.extractUserId()).thenReturn(USER_ID);
    }
    @Test
    void addItemToCart_success() throws Exception {
        CartItemRequestDto req = new CartItemRequestDto(42L, 3);
        CartItemResponseDto res = new CartItemResponseDto(1L, book, 3);

        Mockito.when(shoppingCartService.addToCart(eq(USER_ID), any(CartItemRequestDto.class))).thenReturn(res);

        mockMvc.perform(post(BASE_URL).header(HEADER, USER_ID.toString()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req))).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(res.id())).andExpect(jsonPath("$.quantity").value(res.quantity()));
    }

    @Test
    void getMyCartItems_success() throws Exception {
        CartItemResponseDto res = new CartItemResponseDto(1L, book, 2);

        Mockito.when(shoppingCartService.getCartItems(eq(USER_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(res), PageRequest.of(0, 10), 1));

        mockMvc.perform(get(BASE_URL).header(HEADER, USER_ID.toString()).param("page", "0").param("size", "10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].quantity").value(res.quantity()))
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void getItemById_success() throws Exception {
        CartItemResponseDto res = new CartItemResponseDto(1L, book, 2);
        Mockito.when(shoppingCartService.findItemById(1L, USER_ID)).thenReturn(res);

        mockMvc.perform(get(BASE_URL + "/1").header(HEADER, USER_ID.toString())).andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    void updateCartItem_success() throws Exception {
        CartItemUpdateRequestDto req = new CartItemUpdateRequestDto(5);
        CartItemResponseDto res = new CartItemResponseDto(1L, book, 5);

        Mockito.when(shoppingCartService.updateCartItem(1L, req, USER_ID)).thenReturn(res);

        mockMvc.perform(put(BASE_URL + "/1").header(HEADER, USER_ID.toString()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req))).andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    void deleteCartItem_success() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1").header(HEADER, USER_ID.toString())).andExpect(status().isNoContent());

        Mockito.verify(shoppingCartService).removeFromCart(USER_ID, 1L);
    }

    /* ---------- NEGATIVE FLOWS ---------- */

    @Test
    void addItemToCart_invalidRequest_returnsBadRequest() throws Exception {
        // missing quantity & bookId invalid
        String invalidJson = "{}";

        mockMvc.perform(post(BASE_URL).header(HEADER, USER_ID.toString()).contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)).andExpect(status().isBadRequest());
    }

    @Test
    void getItemById_notFound_returnsNotFound() throws Exception {
        Mockito.when(shoppingCartService.findItemById(99L, USER_ID))
                .thenThrow(new ResourceNotFoundException("Item not found"));

        mockMvc.perform(get(BASE_URL + "/99").header(HEADER, USER_ID.toString())).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Item not found")));
    }

    @Test
    void updateCartItem_notFound_returnsNotFound() throws Exception {
        CartItemUpdateRequestDto req = new CartItemUpdateRequestDto(7);

        Mockito.when(shoppingCartService.updateCartItem(99L, req, USER_ID))
                .thenThrow(new ResourceNotFoundException("Item not found"));

        mockMvc.perform(put(BASE_URL + "/99").header(HEADER, USER_ID.toString()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req))).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Item not found")));
    }
}
