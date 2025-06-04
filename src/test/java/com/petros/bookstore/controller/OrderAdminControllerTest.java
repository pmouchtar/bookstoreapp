package com.petros.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.TestDummyJwtFilter;
import com.petros.bookstore.config.TestSecurityConfig;
import com.petros.bookstore.config.TestServiceConfig;
import com.petros.bookstore.dto.OrderDTO.OrderItemResponseDto;
import com.petros.bookstore.dto.OrderDTO.OrderResponseDto;
import com.petros.bookstore.dto.OrderDTO.OrderStatusUpdateRequestDto;
import com.petros.bookstore.exception.customException.ResourceNotFoundException;
import com.petros.bookstore.enums.Status;
import com.petros.bookstore.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

@WebMvcTest(OrderAdminController.class)
@Import({TestServiceConfig.class, TestSecurityConfig.class, TestDummyJwtFilter.class})
@ActiveProfiles("test")
class OrderAdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    OrderService orderService;

    OrderItemResponseDto item1 = new OrderItemResponseDto(1L, 2L, "Book Title", 15.0, 2, "30.0");
    OrderItemResponseDto item2 = new OrderItemResponseDto(2L, 3L, "Another Book", 25.0, 1, "25");

    @Test
    void getAllOrders_success() throws Exception {
        OrderResponseDto order = new OrderResponseDto(1L, 10L, Status.PENDING, 55.0, Timestamp.from(Instant.now()),
                List.of(item1, item2));

        Mockito.when(orderService.getAllOrders(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/orders").param("page", "0").param("size", "10")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(order.id()))
                .andExpect(jsonPath("$.content[0].status").value(order.status().toString()));
    }

    @Test
    void getOrderById_success() throws Exception {
        OrderResponseDto order = new OrderResponseDto(1L, 10L, Status.PENDING, 55.0, Timestamp.from(Instant.now()),
                List.of(item1, item2));

        Mockito.when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/{orderId}", 1L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.id())).andExpect(jsonPath("$.userId").value(order.userId()))
                .andExpect(jsonPath("$.status").value(order.status().toString()));
    }

    @Test
    void getOrderById_notFound() throws Exception {
        Mockito.when(orderService.getOrderById(999L)).thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get("/orders/{orderId}", 999L)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Order not found")));
    }

    @Test
    void updateOrderStatus_success() throws Exception {
        OrderStatusUpdateRequestDto request = new OrderStatusUpdateRequestDto(Status.DELIVERED);
        OrderResponseDto order = new OrderResponseDto(1L, 10L, Status.DELIVERED, 55.0, Timestamp.from(Instant.now()),
                List.of(item1, item2));

        Mockito.when(orderService.updateOrderStatus(eq(1L), any(OrderStatusUpdateRequestDto.class))).thenReturn(order);

        mockMvc.perform(put("/orders/{orderId}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.id())).andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    void getUserOrders_success() throws Exception {
        OrderResponseDto order = new OrderResponseDto(1L, 10L, Status.PENDING, 55.0, Timestamp.from(Instant.now()),
                List.of(item1, item2));

        Mockito.when(orderService.getOrdersForUser(eq(10L), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/users/{userId}/orders", 10L).param("page", "0").param("size", "10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(order.id()))
                .andExpect(jsonPath("$.content[0].userId").value(order.userId()));
    }
}
