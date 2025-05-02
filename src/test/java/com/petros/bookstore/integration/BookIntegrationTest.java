package com.petros.bookstore.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.petros.bookstore.config.AbstractPostgresContainerTest;
import com.petros.bookstore.config.AbstractPostgresContainerTest;
import com.petros.bookstore.dto.BookRequest;
import com.petros.bookstore.dto.BookResponse;
import com.petros.bookstore.dto.BookUpdateRequest;
import com.petros.bookstore.dto.PaginatedResponse;
import com.petros.bookstore.mapper.BookMapper;
import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.enums.Genre;
import com.petros.bookstore.repository.BookRepository;
import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookIntegrationTest extends AbstractPostgresContainerTest {

    int port = 8080;

//    @Autowired
//    private MockMvc mockMvc;


    @Autowired
    TestRestTemplate client;


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
        // Create book
        ResponseEntity<BookResponse> createResponse = client.postForEntity(
                "/books",
                bookRequest,
                BookResponse.class
        );

        Assertions.assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        BookResponse createdBook = createResponse.getBody();
        Assertions.assertNotNull(createdBook);
        Assertions.assertNotNull(createdBook.getId());
        Assertions.assertEquals("Integration Book", createdBook.getTitle());
        Assertions.assertEquals(Genre.MYSTERY, createdBook.getGenre());

        // Get book by ID
        ResponseEntity<BookResponse> getResponse = client.getForEntity(
                "/books/" + createdBook.getId(),
                BookResponse.class
        );

        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        BookResponse retrievedBook = getResponse.getBody();
        Assertions.assertNotNull(retrievedBook);
        Assertions.assertEquals("Integration Book", retrievedBook.getTitle());
        Assertions.assertEquals("Test Author", retrievedBook.getAuthor());
        Assertions.assertEquals(15.99f, retrievedBook.getPrice());
        Assertions.assertEquals(Genre.MYSTERY, retrievedBook.getGenre());
//        Book book = BookMapper.toEntity(bookRequest);
//        BookResponse response = BookMapper.toResponse(book);
        Assertions.assertEquals(createResponse.getBody(), retrievedBook);
    }


    @Test
    void testGetAllBooks() {
        // Create a book
        ResponseEntity<BookResponse> postResponse = client.postForEntity(
                "/books",
                bookRequest,
                BookResponse.class
        );
        Assertions.assertEquals(HttpStatus.OK, postResponse.getStatusCode());

        // Get all books
        ResponseEntity<PaginatedResponse<BookResponse>> response = client.exchange(
                "/books?page=0&size=10",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PaginatedResponse<BookResponse>>() {}
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        PaginatedResponse<BookResponse> page = response.getBody();
        Assertions.assertNotNull(page);
        Assertions.assertNotNull(page.getContent());
        Assertions.assertEquals(1, page.getContent().size());
    }


    @Test
void testUpdateBook() throws Exception {
    // Create book
    ResponseEntity<BookResponse> createResponse = client.postForEntity(
            "/books",
            bookRequest,
            BookResponse.class
    );

    Assertions.assertEquals(HttpStatus.OK, createResponse.getStatusCode());
    BookResponse createdBook = createResponse.getBody();
    Assertions.assertNotNull(createdBook);
    Long id = createdBook.getId();
    Assertions.assertNotNull(id);

    // Update book
    BookUpdateRequest updateRequest = new BookUpdateRequest();
    updateRequest.setTitle("Updated Book");
    updateRequest.setAvailability(5);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> updateEntity = new HttpEntity<>(
            objectMapper.writeValueAsString(updateRequest),
            headers
    );

    ResponseEntity<BookResponse> updateResponse = client.exchange(
            "/books/" + id,
            HttpMethod.PUT,
            updateEntity,
            BookResponse.class
    );

    Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    BookResponse updatedBook = updateResponse.getBody();
    Assertions.assertNotNull(updatedBook);
    Assertions.assertEquals("Updated Book", updatedBook.getTitle());
    Assertions.assertEquals(5, updatedBook.getAvailability());
}


    @Test
    void testDeleteBook() throws Exception {
        // Create book
        ResponseEntity<BookResponse> createResponse = client.postForEntity(
                "/books",
                bookRequest,
                BookResponse.class
        );

        Assertions.assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        BookResponse createdBook = createResponse.getBody();
        Assertions.assertNotNull(createdBook);
        Long id = createdBook.getId();
        Assertions.assertNotNull(id);

        // Delete book
        ResponseEntity<Void> deleteResponse = client.exchange(
                "/books/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Confirm deletion
        ResponseEntity<String> getResponse = client.getForEntity(
                "/books/" + id,
                String.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

@Test
void testSearchBooks_withDeserializationToPage() {
    // Create books
    client.postForEntity("/books", bookRequest, BookResponse.class);

    BookRequest secondBook = new BookRequest("Another Title", "Other Author", "Desc", 12.99f, 15, Genre.MYSTERY);
    client.postForEntity("/books", secondBook, BookResponse.class);

    // Search params
    String url = "/books?title=Integration&author=Test&availability=5&genre=MYSTERY&minPrice=10.0&maxPrice=20.0&page=0&size=10";

    ResponseEntity<PaginatedResponse<BookResponse>> response = client.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<PaginatedResponse<BookResponse>>() {}
    );

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    PaginatedResponse<BookResponse> page = response.getBody();
    Assertions.assertNotNull(page);
    Assertions.assertEquals(1, page.getContent().size());
    Assertions.assertEquals("Integration Book", page.getContent().get(0).getTitle());
}


    @Test
    void testSearchBooks_InvalidPriceType() {
        // Search with invalid price type
        String url = "/books?minPrice=cheap&page=0&size=10";

        ResponseEntity<String> response = client.exchange(
                url,
                HttpMethod.GET,
                null,
                String.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(responseBody.contains("Invalid value 'cheap' for parameter 'minPrice'. Expected type: Float."));
    }



    @Test
    void testGetBookById_NotFound() {
        // Get book by invalid ID (99999)
        ResponseEntity<String> response = client.getForEntity(
                "/books/99999",
                String.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(responseBody.contains("Book with ID 99999 not found."));
    }


    @Test
    void testDeleteBookById_NotFound() {
        // Delete book with non-existent ID (99999)
        ResponseEntity<String> response = client.exchange(
                "/books/99999",
                HttpMethod.DELETE,
                null,
                String.class
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(responseBody.contains("Book with ID 99999 not found."));
    }


    @Test
    void testCreateBook_InvalidData() {
        // Create a book with invalid data
        BookRequest invalidRequest = new BookRequest("", "", "", -1.0f, -10, null);

        // Send request and expect BAD_REQUEST status
        ResponseEntity<String> response = client.postForEntity(
                "/books",
                invalidRequest,
                String.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    void testCreateBook_MalformedJSON() {
        // Malformed JSON input
        String malformedJson = "{\"price\": \"abc\" }";

        ResponseEntity<String> response = client.exchange(
                "/books",
                HttpMethod.POST,
                new HttpEntity<>(malformedJson, createJsonHeaders()),
                String.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(responseBody.contains("Malformed JSON request or invalid data format"));
    }


    @Test
    void testUpdateBook_MalformedJson() {
        // Create book
        ResponseEntity<BookResponse> postResponse = client.postForEntity(
                "/books",
                bookRequest,
                BookResponse.class
        );
        Assertions.assertEquals(HttpStatus.OK, postResponse.getStatusCode());
        Long id = postResponse.getBody().getId();  // Assuming getId() is available in BookResponse

        // Malformed update request
        String malformedUpdate = "{ \"availability\": \"ten\" }";

        ResponseEntity<String> response = client.exchange(
                "/books/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(malformedUpdate, createJsonHeaders()),
                String.class
        );

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
