package com.petros.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.TestDummyJwtFilter;
import com.petros.bookstore.config.TestSecurityConfig;
import com.petros.bookstore.config.TestServiceConfig;
import com.petros.bookstore.dto.UserDTO.UserProfileResponseDto;
import com.petros.bookstore.dto.UserDTO.UserProfileUpdateRequestDto;
import com.petros.bookstore.exception.customException.ResourceNotFoundException;
import com.petros.bookstore.model.User;
import com.petros.bookstore.model.enums.Role;
import com.petros.bookstore.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserProfileController.class)
@Import({TestServiceConfig.class, TestSecurityConfig.class, TestDummyJwtFilter.class})
@ActiveProfiles("test")
class UserProfileControllerTest {

    private static final String HEADER   = "X-USER-ID";
    private static final Long   USER_ID  = 5L;
    private static final String BASE_URL = "/users/me";

    @Autowired MockMvc       mockMvc;
    @Autowired ObjectMapper  mapper;
    @Autowired UserService   userService;   // mock


    @Test
    void getUserProfile_success() throws Exception {
        UserProfileResponseDto dto = new UserProfileResponseDto(USER_ID, "John", "Doe", "jdoe", Role.USER);
        Mockito.when(userService.findUserById(USER_ID)).thenReturn(dto);

        mockMvc.perform(get(BASE_URL).header(HEADER, USER_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jdoe"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getUserProfile_notFound_returns404() throws Exception {
        Mockito.when(userService.findUserById(USER_ID))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get(BASE_URL).header(HEADER, USER_ID.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("User not found")));
    }


    @Test
    void updateUserProfile_success() throws Exception {
        UserProfileUpdateRequestDto req = new UserProfileUpdateRequestDto("Jane", "Doe", "janed", "secretPassword1");
        User dummyEntity = new User();
        dummyEntity.setId(USER_ID);
        dummyEntity.setFirstName("Jane");
        dummyEntity.setLastName("Doe");
        dummyEntity.setUsername("janed");
        dummyEntity.setPassword("secretPassword1");
        dummyEntity.setRole(Role.USER);

        Mockito.when(userService.updateUserProfile(eq(USER_ID), any()))
                .thenReturn(dummyEntity);

        mockMvc.perform(put(BASE_URL)
                        .header(HEADER, USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.username").value("janed"));
    }

    @Test
    void updateUserProfile_invalidRequest_returns400() throws Exception {
        User dummyEntity = new User();
        dummyEntity.setId(USER_ID);
        dummyEntity.setFirstName("Jane");
        dummyEntity.setLastName("Doe");
        dummyEntity.setUsername("janed");
        dummyEntity.setPassword("secretPassword1");
        dummyEntity.setRole(Role.USER);

        Mockito.when(userService.updateUserProfile(eq(USER_ID), any()))
                .thenReturn(dummyEntity);

        String invalidJson = """
            { "firstName": "Foo", "lastName": "Bar", "password": "invalid" }
            """; //invalid password

        mockMvc.perform(put(BASE_URL)
                        .header(HEADER, USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    void deleteUserProfile_success() throws Exception {
        Mockito.doReturn(true)
                .when(userService).deleteUserById(USER_ID);
        mockMvc.perform(delete(BASE_URL).header(HEADER, USER_ID.toString()))
                .andExpect(status().isNoContent());

    }

    @Test
    void deleteUserProfile_notFound_returns404() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("User not found"))
                .when(userService).deleteUserById(USER_ID);

        mockMvc.perform(delete(BASE_URL).header(HEADER, USER_ID.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("User not found")));
    }
}
