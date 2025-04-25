package com.petros.bookstore.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.dto.BookRequest;
import com.petros.bookstore.dto.BookUpdateRequest;
import com.petros.bookstore.model.enums.Genre;
import com.petros.bookstore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private BookRequest bookRequest;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();

        bookRequest = new BookRequest("Integration Book", "Test Author", "A book for testing", 15.99f, 10, Genre.MYSTERY);
    }

    @Test
    void testCreateAndGetBook() throws Exception {
        // Create
        MvcResult createResult = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).get("id").asLong();

        // Get by ID
        mockMvc.perform(get("/books/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Book"))
                .andExpect(jsonPath("$.author").value("Test Author"));
    }

    @Test
    void testGetAllBooks() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/books?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void testUpdateBook() throws Exception {
        // Create book
        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        // Update
        BookUpdateRequest updateRequest = new BookUpdateRequest();
        updateRequest.setTitle("Updated Book");
        updateRequest.setAvailability(5);

        mockMvc.perform(put("/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"))
                .andExpect(jsonPath("$.availability").value(5));
    }

    @Test
    void testDeleteBook() throws Exception {
        // Create book
        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        // Delete
        mockMvc.perform(delete("/books/" + id))
                .andExpect(status().isNoContent());

        // Confirm deletion
        mockMvc.perform(get("/books/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchBooks() throws Exception {
        // Add two books
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk());

        BookRequest secondBook = new BookRequest("Another Title", "Other Author", "Desc", 12.99f, 15, Genre.MYSTERY);
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondBook)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/books")
                        .param("title", "Integration")
                        .param("author", "Test")
                        .param("availability", "5")
                        .param("genre", "MYSTERY")
                        .param("minPrice", "10.0")
                        .param("maxPrice", "20.0")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Integration Book"));
    }

    @Test
    void testGetBookById_NotFound() throws Exception {
        mockMvc.perform(get("/books/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with ID 99999 not found."));
    }

    @Test
    void testDeleteBookById_NotFound() throws Exception {
        mockMvc.perform(delete("/books/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with ID 99999 not found."));
    }

    @Test
    void testCreateBook_InvalidData() throws Exception {
        BookRequest invalidRequest = new BookRequest("", "", "", -1.0f, -10, null);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateBook_MalformedJson() throws Exception {
        String malformedJson = "{\"price\": \"abc\" }";

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed JSON request or invalid data format"));
    }

    @Test
    void testUpdateBook_MalformedJson() throws Exception {
        MvcResult result = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        String malformedUpdate = "{ \"availability\": \"ten\" }";

        mockMvc.perform(put("/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedUpdate))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed JSON request or invalid data format"));
    }
}
