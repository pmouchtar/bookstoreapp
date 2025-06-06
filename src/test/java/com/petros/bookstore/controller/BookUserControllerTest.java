package com.petros.bookstore.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.TestSecurityConfig;
import com.petros.bookstore.dto.bookdto.BookResponseDto;
import com.petros.bookstore.exception.customException.ResourceNotFoundException;
import com.petros.bookstore.enums.Genre;
import com.petros.bookstore.repository.BookRepository;
import com.petros.bookstore.service.BookService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BookUserController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class BookUserControllerTest {

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
    @DisplayName("GET /books - search by title and genre - books found")
    void testSearchBooksFound() throws Exception {
        BookResponseDto book1 = new BookResponseDto(1L, "Title 1", "Author 1", "Description", 12.99, 10,
                Genre.SCIENCE_FICTION);
        BookResponseDto book2 = new BookResponseDto(2L, "Title 2", "Author 2", "Description", 15.99, 5, Genre.FANTASY);
        List<BookResponseDto> books = Arrays.asList(book1, book2);
        Page<BookResponseDto> page = new PageImpl<>(books, PageRequest.of(0, 10), books.size());

        when(bookService.searchBooks(any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/books").param("title", "Title").param("genre", "SCIENCE_FICTION")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L)).andExpect(jsonPath("$.content[1].id").value(2L));
    }

    @Test
    @DisplayName("GET /books - search by title and genre - no books found")
    void testSearchBooksNotFound() throws Exception {
        Page<BookResponseDto> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(bookService.searchBooks(any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/books").param("title", "Nonexistent").param("genre", "SCIENCE_FICTION")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @DisplayName("GET /books/{id} - found")
    void testGetBookById() throws Exception {
        BookResponseDto response = new BookResponseDto(1L, "Title", "Author", "Desc", 10.99, 5, Genre.SCIENCE_FICTION);

        when(bookService.findBookById(1L)).thenReturn(response);

        mockMvc.perform(get("/books/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("GET /books/{bookId} - not found")
    void testGetBookNotFound() throws Exception {
        Long invalidBookId = 999L;

        when(bookService.findBookById(invalidBookId))
                .thenThrow(new ResourceNotFoundException("Book with ID " + invalidBookId + " not found"));

        mockMvc.perform(get("/books/{bookId}", invalidBookId)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with ID " + invalidBookId + " not found"));
    }
}
