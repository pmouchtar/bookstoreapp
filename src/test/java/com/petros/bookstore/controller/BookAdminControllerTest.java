package com.petros.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.TestSecurityConfig;
import com.petros.bookstore.dto.bookdto.BookRequestDto;
import com.petros.bookstore.dto.bookdto.BookResponseDto;
import com.petros.bookstore.dto.bookdto.BookUpdateRequestDto;
import com.petros.bookstore.enums.Genre;
import com.petros.bookstore.repository.BookRepository;
import com.petros.bookstore.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookAdminController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class BookAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;

    @TestConfiguration
    static class Config {

        @Bean
        public BookRepository bookRepository() {
            return Mockito.mock(BookRepository.class);
        }

        @Bean
        public BookService bookService() {
            return Mockito.mock(BookService.class);
        }
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    private static String asJsonString(final Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("POST /books - success")
    void testAddBook() throws Exception {
        BookRequestDto request = new BookRequestDto("Title", "Author", "Description", 12.99, 10, Genre.SCIENCE_FICTION);
        BookResponseDto response = new BookResponseDto(1L, "Title", "Author", "Description", 12.99, 10,
                Genre.SCIENCE_FICTION);

        when(bookService.save(any(BookRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(asJsonString(request)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    @DisplayName("PUT /books/{id} - update")
    void testUpdateBook() throws Exception {
        BookUpdateRequestDto updateRequest = new BookUpdateRequestDto("Updated Title", null, null, null, null, null);

        BookResponseDto updatedResponse = new BookResponseDto(1L, "Updated Title", "Author", "Desc", 10.99, 5,
                Genre.SCIENCE_FICTION);

        when(bookService.updateBook(Mockito.eq(1L), any(BookUpdateRequestDto.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/books/1").contentType(MediaType.APPLICATION_JSON).content(asJsonString(updateRequest)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @DisplayName("DELETE /books/{id} - success")
    void testDeleteBook() throws Exception {
        when(bookService.deleteBookById(1L)).thenReturn(true);

        mockMvc.perform(delete("/books/1")).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /books/{id} - not found")
    void testDeleteBookNotFound() throws Exception {
        when(bookService.deleteBookById(99L)).thenReturn(false);

        mockMvc.perform(delete("/books/99")).andExpect(status().isNoContent());
    }
}
