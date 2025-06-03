package com.petros.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.TestDummyJwtFilter;
import com.petros.bookstore.config.TestSecurityConfig;
import com.petros.bookstore.config.TestServiceConfig;
import com.petros.bookstore.dto.OrderDTO.OrderResponseDto;
import com.petros.bookstore.exception.customException.ResourceNotFoundException;
import com.petros.bookstore.model.enums.Status;
import com.petros.bookstore.service.OrderService;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderUserController.class)
@Import({TestServiceConfig.class, TestSecurityConfig.class, TestDummyJwtFilter.class})
@ActiveProfiles("test")
class OrderUserControllerTest {

    private static final String HEADER = "X-USER-ID";
    private static final Long USER_ID = 777L;
    private static final String BASE_URL = "/users/me/orders";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    OrderService orderService;

    @Test
    void placeOrder_success() throws Exception {
        OrderResponseDto orderResponse = sampleOrderResponseDto();

        Mockito.when(orderService.placeOrder(eq(USER_ID), any(Pageable.class))).thenReturn(orderResponse);

        mockMvc.perform(post(BASE_URL).header(HEADER, USER_ID.toString()).param("page", "0").param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderResponse.id()))
                .andExpect(jsonPath("$.userId").value(orderResponse.userId()))
                .andExpect(jsonPath("$.status").value(orderResponse.status().toString()));
    }

    @Test
    void myOrders_success() throws Exception {
        OrderResponseDto orderResponse = sampleOrderResponseDto();

        Mockito.when(orderService.getOrdersForUser(eq(USER_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(orderResponse), PageRequest.of(0, 10), 1));

        mockMvc.perform(get(BASE_URL).header(HEADER, USER_ID.toString()).param("page", "0").param("size", "10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(orderResponse.id()))
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void myOrder_success() throws Exception {
        OrderResponseDto orderResponse = sampleOrderResponseDto();

        Mockito.when(orderService.getOrderForUser(1L, USER_ID)).thenReturn(orderResponse);

        mockMvc.perform(get(BASE_URL + "/1").header(HEADER, USER_ID.toString())).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderResponse.id()))
                .andExpect(jsonPath("$.status").value(orderResponse.status().toString()));
    }

    @Test
    void myOrder_notFound_returns404() throws Exception {
        Mockito.when(orderService.getOrderForUser(999L, USER_ID))
                .thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get(BASE_URL + "/999").header(HEADER, USER_ID.toString())).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Order not found")));
    }

    @Test
    void placeOrder_badRequest_returns400() throws Exception {
        Mockito.when(orderService.placeOrder(eq(USER_ID), any(Pageable.class)))
                .thenThrow(new IllegalArgumentException("Invalid order request"));

        mockMvc.perform(post(BASE_URL).header(HEADER, USER_ID.toString()).param("page", "0").param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    // helper method
    private OrderResponseDto sampleOrderResponseDto() {
        Timestamp timestampNow = Timestamp.from(Instant.now());
        return new OrderResponseDto(1L, USER_ID, Status.PENDING, 123.45, timestampNow, List.of());
    }
}
