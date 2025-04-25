package com.petros.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.dto.BookRequest;
import com.petros.bookstore.dto.BookResponse;
import com.petros.bookstore.dto.BookUpdateRequest;
import com.petros.bookstore.exception.ResourceNotFoundException;
import com.petros.bookstore.model.enums.Genre;
import com.petros.bookstore.repository.BookRepository;
import com.petros.bookstore.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(BookControllerTest.Config.class)
class BookControllerTest {

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
        BookResponse book1 = new BookResponse(1L, "Title 1", "Author 1", "Description", 12.99f, 10, Genre.SCIENCE_FICTION);
        BookResponse book2 = new BookResponse(2L, "Title 2", "Author 2", "Description", 15.99f, 5, Genre.FANTASY);
        List<BookResponse> books = Arrays.asList(book1, book2);
        Page<BookResponse> page = new PageImpl<>(books, PageRequest.of(0, 10), books.size());

        when(bookService.searchBooks(any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/books")
                        .param("title", "Title")
                        .param("genre", "SCIENCE_FICTION")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L));
    }

    @Test
    @DisplayName("GET /books - search by title and genre - no books found")
    void testSearchBooksNotFound() throws Exception {
        Page<BookResponse> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(bookService.searchBooks(any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/books")
                        .param("title", "Nonexistent")
                        .param("genre", "SCIENCE_FICTION")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @DisplayName("POST /books - success")
    void testAddBook() throws Exception {
        BookRequest request = new BookRequest("Title", "Author", "Description", 12.99f, 10, Genre.SCIENCE_FICTION);
        BookResponse response = new BookResponse(1L, "Title", "Author", "Description", 12.99f, 10, Genre.SCIENCE_FICTION);

        when(bookService.save(any(BookRequest.class))).thenReturn(response);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    @DisplayName("GET /books/{id} - found")
    void testGetBookById() throws Exception {
        BookResponse response = new BookResponse(1L, "Title", "Author", "Desc", 10.99f, 5, Genre.SCIENCE_FICTION);

        when(bookService.findBookById(1L)).thenReturn(response);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("GET /books/{bookId} - not found")
    void testGetBookNotFound() throws Exception {
        Long invalidBookId = 999L;

        when(bookService.findBookById(invalidBookId)).thenThrow(new ResourceNotFoundException("Book with ID " + invalidBookId + " not found"));

        mockMvc.perform(get("/books/{bookId}", invalidBookId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with ID " + invalidBookId + " not found"));
    }

    @Test
    @DisplayName("PUT /books/{id} - update")
    void testUpdateBook() throws Exception {
        BookUpdateRequest updateRequest = new BookUpdateRequest();
        updateRequest.setTitle("Updated Title");

        BookResponse updatedResponse = new BookResponse(1L, "Updated Title", "Author", "Desc", 10.99f, 5, Genre.SCIENCE_FICTION);

        when(bookService.updateBook(Mockito.eq(1L), any(BookUpdateRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @DisplayName("DELETE /books/{id} - success")
    void testDeleteBook() throws Exception {
        when(bookService.deleteBookById(1L)).thenReturn(true);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /books/{id} - not found")
    void testDeleteBookNotFound() throws Exception {
        when(bookService.deleteBookById(99L)).thenReturn(false);

        mockMvc.perform(delete("/books/99"))
                .andExpect(status().isNoContent());
    }
}
