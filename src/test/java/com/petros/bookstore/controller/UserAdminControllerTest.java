package com.petros.bookstore.controller;

import com.petros.bookstore.config.TestDummyJwtFilter;
import com.petros.bookstore.config.TestSecurityConfig;
import com.petros.bookstore.config.TestServiceConfig;
import com.petros.bookstore.dto.UserDTO.UserAdminUpdateRequestDto;
import com.petros.bookstore.dto.UserDTO.UserProfileResponseDto;
import com.petros.bookstore.model.enums.Role;
import com.petros.bookstore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserAdminController.class)
@Import({TestServiceConfig.class, TestSecurityConfig.class, TestDummyJwtFilter.class})
@ActiveProfiles("test")
class UserAdminControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserService userService;

    private UserProfileResponseDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserProfileResponseDto(1L, "John", "Doe", "johndoe", Role.USER);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_shouldReturnPageOfUsers() throws Exception {
        when(userService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(userDto), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/users").param("page", "0").param("size", "10")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("johndoe"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchUsers_shouldReturnFilteredPage() throws Exception {
        when(userService.searchUsers(eq("johndoe"), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(userDto)));

        mockMvc.perform(get("/users").param("username", "johndoe")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("johndoe"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUser_shouldReturnUserDto() throws Exception {
        when(userService.findUserById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1")).andExpect(status().isOk()).andExpect(jsonPath("$.username").value("johndoe"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserAdminUpdateRequestDto updateRequest = new UserAdminUpdateRequestDto("Jane", "Smith", Role.ADMIN);

        when(userService.updateUserById(eq(1L), any(UserAdminUpdateRequestDto.class)))
                .thenReturn(new UserProfileResponseDto(1L, "Jane", "Smith", "johndoe", Role.ADMIN));

        mockMvc.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))).andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane")).andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/1")).andExpect(status().isNoContent());
    }
}
