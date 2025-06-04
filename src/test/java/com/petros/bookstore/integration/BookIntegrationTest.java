package com.petros.bookstore.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.AbstractPostgresContainerTest;
import com.petros.bookstore.dto.BookDTO.BookRequestDto;
import com.petros.bookstore.dto.BookDTO.BookResponseDto;
import com.petros.bookstore.dto.BookDTO.BookUpdateRequestDto;
import com.petros.bookstore.dto.PaginationDTO.PaginatedResponseDto;
import com.petros.bookstore.enums.Genre;
import com.petros.bookstore.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/** Integration tests for shopping-cart endpoints using TestRestTemplate. */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class BookIntegrationTest extends AbstractPostgresContainerTest {

    int port = 8080;

    @Autowired
    TestRestTemplate client;

    @Autowired
    private ObjectMapper objectMapper;

    private BookRequestDto bookRequestDto;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();

        bookRequestDto = new BookRequestDto("Integration Book", "Test Author", "A book for testing", 15.99, 10,
                Genre.MYSTERY);
    }

    @Test
    void testCreateAndGetBook() throws Exception {
        // Create book
        ResponseEntity<BookResponseDto> createResponse = client.postForEntity("/books", bookRequestDto,
                BookResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        BookResponseDto createdBook = createResponse.getBody();
        Assertions.assertNotNull(createdBook);
        Assertions.assertNotNull(createdBook.id());
        Assertions.assertEquals("Integration Book", createdBook.title());
        Assertions.assertEquals(Genre.MYSTERY, createdBook.genre());

        // Get book by ID
        ResponseEntity<BookResponseDto> getResponse = client.getForEntity("/books/" + createdBook.id(),
                BookResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        BookResponseDto retrievedBook = getResponse.getBody();
        Assertions.assertNotNull(retrievedBook);
        Assertions.assertEquals("Integration Book", retrievedBook.title());
        Assertions.assertEquals("Test Author", retrievedBook.author());
        Assertions.assertEquals(15.99, retrievedBook.price());
        Assertions.assertEquals(Genre.MYSTERY, retrievedBook.genre());
        Assertions.assertEquals(createResponse.getBody(), retrievedBook);
    }

    @Test
    void testGetAllBooks() {
        // Create a book
        ResponseEntity<BookResponseDto> postResponse = client.postForEntity("/books", bookRequestDto,
                BookResponseDto.class);
        Assertions.assertEquals(HttpStatus.OK, postResponse.getStatusCode());

        // Get all books
        ResponseEntity<PaginatedResponseDto<BookResponseDto>> response = client.exchange("/books?page=0&size=10",
                HttpMethod.GET, null, new ParameterizedTypeReference<PaginatedResponseDto<BookResponseDto>>() {
                });

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        PaginatedResponseDto<BookResponseDto> page = response.getBody();
        Assertions.assertNotNull(page);
        Assertions.assertNotNull(page.getContent());
        Assertions.assertEquals(1, page.getContent().size());
    }

    @Test
    void testUpdateBook() throws Exception {
        // Create book
        ResponseEntity<BookResponseDto> createResponse = client.postForEntity("/books", bookRequestDto,
                BookResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        BookResponseDto createdBook = createResponse.getBody();
        Assertions.assertNotNull(createdBook);
        Long id = createdBook.id();
        Assertions.assertNotNull(id);

        // Update book
        BookUpdateRequestDto updateRequest = new BookUpdateRequestDto("Updated Book", null, null, null, 5, null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> updateEntity = new HttpEntity<>(objectMapper.writeValueAsString(updateRequest), headers);

        ResponseEntity<BookResponseDto> updateResponse = client.exchange("/books/" + id, HttpMethod.PUT, updateEntity,
                BookResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        BookResponseDto updatedBook = updateResponse.getBody();
        Assertions.assertNotNull(updatedBook);
        Assertions.assertEquals("Updated Book", updatedBook.title());
        Assertions.assertEquals(5, updatedBook.availability());
    }

    @Test
    void testDeleteBook() throws Exception {
        // Create book
        ResponseEntity<BookResponseDto> createResponse = client.postForEntity("/books", bookRequestDto,
                BookResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        BookResponseDto createdBook = createResponse.getBody();
        Assertions.assertNotNull(createdBook);
        Long id = createdBook.id();
        Assertions.assertNotNull(id);

        // Delete book
        ResponseEntity<Void> deleteResponse = client.exchange("/books/" + id, HttpMethod.DELETE, null, Void.class);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Confirm deletion
        ResponseEntity<String> getResponse = client.getForEntity("/books/" + id, String.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void testSearchBooks_withDeserializationToPage() {
        // Create books
        client.postForEntity("/books", bookRequestDto, BookResponseDto.class);

        BookRequestDto secondBook = new BookRequestDto("Another Title", "Other Author", "Desc", 12.99, 15,
                Genre.MYSTERY);
        client.postForEntity("/books", secondBook, BookResponseDto.class);

        // Search params
        String url = "/books?title=Integration&author=Test&availability=5&genre=MYSTERY&minPrice=10.0&maxPrice=20.0&page=0&size=10";

        ResponseEntity<PaginatedResponseDto<BookResponseDto>> response = client.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<PaginatedResponseDto<BookResponseDto>>() {
                });

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        PaginatedResponseDto<BookResponseDto> page = response.getBody();
        Assertions.assertNotNull(page);
        Assertions.assertEquals(1, page.getContent().size());
        Assertions.assertEquals("Integration Book", page.getContent().get(0).title());
    }

    @Test
    void testSearchBooks_InvalidPriceType() {
        // Search with invalid price type
        String url = "/books?minPrice=cheap&page=0&size=10";

        ResponseEntity<String> response = client.exchange(url, HttpMethod.GET, null, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(
                responseBody.contains("Invalid value 'cheap' for parameter 'minPrice'. Expected type: Double."));
    }

    @Test
    void testGetBookById_NotFound() {
        // Get book by invalid ID (99999)
        ResponseEntity<String> response = client.getForEntity("/books/99999", String.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(responseBody.contains("Book with ID 99999 not found."));
    }

    @Test
    void testDeleteBookById_NotFound() {
        // Delete book with non-existent ID (99999)
        ResponseEntity<String> response = client.exchange("/books/99999", HttpMethod.DELETE, null, String.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(responseBody.contains("Book with ID 99999 not found."));
    }

    @Test
    void testCreateBook_InvalidData() {
        // Create a book with invalid data
        BookRequestDto invalidRequest = new BookRequestDto("", "", "", -1.0, -10, null);

        // Send request and expect BAD_REQUEST status
        ResponseEntity<String> response = client.postForEntity("/books", invalidRequest, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateBook_MalformedJSON() {
        // Malformed JSON input
        String malformedJson = "{\"price\": \"abc\" }";

        ResponseEntity<String> response = client.exchange("/books", HttpMethod.POST,
                new HttpEntity<>(malformedJson, createJsonHeaders()), String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(responseBody.contains("Malformed JSON request or invalid data format"));
    }

    @Test
    void testUpdateBook_MalformedJson() {
        // Create book
        ResponseEntity<BookResponseDto> postResponse = client.postForEntity("/books", bookRequestDto,
                BookResponseDto.class);
        Assertions.assertEquals(HttpStatus.OK, postResponse.getStatusCode());
        Long id = postResponse.getBody().id();

        // Malformed update request
        String malformedUpdate = "{ \"availability\": \"ten\" }";

        ResponseEntity<String> response = client.exchange("/books/" + id, HttpMethod.PUT,
                new HttpEntity<>(malformedUpdate, createJsonHeaders()), String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(responseBody.contains("Malformed JSON request or invalid data format"));
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
