// package com.petros.bookstore.integration;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.petros.bookstore.config.AbstractPostgresContainerTest;
// import com.petros.bookstore.dto.FavouriteBookRequest;
// import com.petros.bookstore.model.Book;
// import com.petros.bookstore.model.User;
// import com.petros.bookstore.model.enums.Role;
// import com.petros.bookstore.repository.BookRepository;
// import com.petros.bookstore.repository.FavouriteBookRepository;
// import com.petros.bookstore.repository.UserRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.context.SecurityContext;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.oauth2.jwt.Jwt;
// import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
//
// import java.time.Instant;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// class FavouriteBookIntegrationTest extends AbstractPostgresContainerTest {
//
//    @Autowired private MockMvc mockMvc;
//    @Autowired private ObjectMapper objectMapper;
//    @Autowired private UserRepository userRepository;
//    @Autowired private BookRepository bookRepository;
//    @Autowired private PasswordEncoder passwordEncoder;
//    @Autowired private FavouriteBookRepository favouriteBookRepository;
//
//    private Long userId;
//    private Long adminId;
//    private Long bookId;
//
//    private void mockAuthentication(Long userId, String role) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", userId);
//
//        Jwt jwt = new Jwt(
//                "fake-token",
//                Instant.now(),
//                Instant.now().plusSeconds(3_600),
//                Map.of("alg", "none"),
//                claims
//        );
//
//        List<GrantedAuthority> authorities = List.of(() -> "ROLE_" + role);
//        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, authorities, "user-" +
// userId);
//        auth.setAuthenticated(true);
//
//        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
//        ctx.setAuthentication(auth);
//        SecurityContextHolder.setContext(ctx);
//    }
//
//    @BeforeEach
//    void setUp() {
//        // DB reset
//        userRepository.deleteAll();
//        bookRepository.deleteAll();
//
//        User user = new User();
//        user.setFirstName("Petros");
//        user.setLastName("Papadopoulos");
//        user.setUsername("petrosdev");
//        user.setPassword(passwordEncoder.encode("Secure123"));
//        user.setRole(Role.USER);
//        userId = userRepository.save(user).getId();
//
//        User admin = new User();
//        admin.setFirstName("Admin");
//        admin.setLastName("Boss");
//        admin.setUsername("adminboss");
//        admin.setPassword(passwordEncoder.encode("Admin123"));
//        admin.setRole(Role.ADMIN);
//        adminId = userRepository.save(admin).getId();
//
//        Book book = new Book();
//        book.setTitle("Integration Testing 101");
//        book.setAuthor("Captain Stub");
//        book.setDescription("description");
//        book.setPrice(9.99);
//        book.setAvailability(10);
//        book.setGenre(com.petros.bookstore.model.enums.Genre.SCIENCE_FICTION);
//        bookId = bookRepository.save(book).getId();
//    }
//
//    @Test
//    void addFavouriteBook_ShouldReturn200AndPersist() throws Exception {
//        mockAuthentication(userId, "USER");
//
//        FavouriteBookRequest req = new FavouriteBookRequest(bookId);
//
//        mockMvc.perform(post("/users/me/favourite-books")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.bookId").value(bookId));
//
//        assertThat(bookRepository.findById(bookId)).isPresent();
//    }
//
//    @Test
//    void getMyFavouriteBooks_ShouldReturnPageWithOneEntry() throws Exception {
//        addFavouriteDirectly();
//
//        mockAuthentication(userId, "USER");
//
//        mockMvc.perform(get("/users/me/favourite-books"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content.length()").value(1))
//                .andExpect(jsonPath("$.content[0].bookId").value(bookId));
//    }
//
//    @Test
//    void deleteFavouriteBook_ShouldReturn204AndRemove() throws Exception {
//        addFavouriteDirectly();
//
//        mockAuthentication(userId, "USER");
//
//        mockMvc.perform(delete("/users/me/favourite-books/{id}", bookId))
//                .andExpect(status().isNoContent());
//
//        mockMvc.perform(get("/users/me/favourite-books"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content.length()").value(0));
//    }
//
//    //admin get endpoint
//    @Test
//    void adminGetUserFavouriteBooks_ShouldReturnUserPage() throws Exception {
//        addFavouriteDirectly();
//
//        mockAuthentication(adminId, "ADMIN");
//
//        mockMvc.perform(get("/users/{userId}/favourite-books", userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content.length()").value(1))
//                .andExpect(jsonPath("$.content[0].bookId").value(bookId));
//    }
//
//    private void addFavouriteDirectly() throws Exception {
//        mockAuthentication(userId, "USER");
//
//        FavouriteBookRequest req = new FavouriteBookRequest(bookId);
//        mockMvc.perform(post("/users/me/favourite-books")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk());
//        SecurityContextHolder.clearContext();
//    }
//
//    @Test
//    void testAddFavouriteBook_BookDoesNotExist_ShouldReturn404() throws Exception {
//        mockAuthentication(userId, "USER");
//
//        FavouriteBookRequest request = new FavouriteBookRequest(9999L);
//
//        mockMvc.perform(post("/users/me/favourite-books")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void testAddFavouriteBook_AlreadyExists_ShouldReturn400() throws Exception {
//        mockAuthentication(userId, "USER");
//
//        FavouriteBookRequest request = new FavouriteBookRequest(bookId);
//
//        mockMvc.perform(post("/users/me/favourite-books")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.bookId").value(bookId));
//
//        mockMvc.perform(post("/users/me/favourite-books")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isConflict());
//    }
//
//    @Test
//    void testAddFavouriteBook_InvalidRequest_ShouldReturn400() throws Exception {
//        mockAuthentication(userId, "USER");
//
//        // bookId is null
//        FavouriteBookRequest request = new FavouriteBookRequest(null);
//
//        mockMvc.perform(post("/users/me/favourite-books")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testDeleteFavouriteBook_NotExists_ShouldReturn404() throws Exception {
//        mockAuthentication(userId, "USER");
//
//        mockMvc.perform(delete("/users/me/favourite-books/{bookId}", 9999L))
//                .andExpect(status().isNotFound());
//    }
// }

package com.petros.bookstore.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.AbstractPostgresContainerTest;
import com.petros.bookstore.dto.FavouriteBookDTO.FavouriteBookRequestDto;
import com.petros.bookstore.dto.FavouriteBookDTO.FavouriteBookResponseDto;
import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.User;
import com.petros.bookstore.model.enums.Genre;
import com.petros.bookstore.model.enums.Role;
import com.petros.bookstore.repository.BookRepository;
import com.petros.bookstore.repository.FavouriteBookRepository;
import com.petros.bookstore.repository.UserRepository;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for favourite-book endpoints using TestRestTemplate. Relies
 * on DummyJwtFilter + TestSecurityConfig that injects auth from header
 * X-USER-ID.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FavouriteBookIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private FavouriteBookRepository favouriteBookRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long userId;
    private Long adminId;
    private Long bookId;

    private HttpHeaders userHeaders;
    private HttpHeaders adminHeaders;

    @BeforeEach
    void setUp() {
        favouriteBookRepository.deleteAll();
        userRepository.deleteAll();
        bookRepository.deleteAll();

        // normal USER
        User user = new User();
        user.setFirstName("Petros");
        user.setLastName("Papadopoulos");
        user.setUsername("petrosdev");
        user.setPassword(passwordEncoder.encode("Secure123"));
        user.setRole(Role.USER);
        userId = userRepository.save(user).getId();

        // ADMIN
        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("Boss");
        admin.setUsername("adminboss");
        admin.setPassword(passwordEncoder.encode("Admin123"));
        admin.setRole(Role.ADMIN);
        adminId = userRepository.save(admin).getId();

        // a book
        Book book = new Book();
        book.setTitle("Integration Testing 101");
        book.setAuthor("Captain Stub");
        book.setDescription("desc");
        book.setPrice(9.99);
        book.setAvailability(10);
        book.setGenre(Genre.SCIENCE_FICTION);
        bookId = bookRepository.save(book).getId();

        userHeaders = new HttpHeaders();
        userHeaders.add("X-USER-ID", userId.toString());

        adminHeaders = new HttpHeaders();
        adminHeaders.add("X-USER-ID", adminId.toString());
    }

    @Test
    void addFavouriteBook_ShouldPersistAndReturn200() {
        FavouriteBookRequestDto req = new FavouriteBookRequestDto(bookId);

        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FavouriteBookRequestDto> entity = new HttpEntity<>(req, userHeaders);

        ResponseEntity<FavouriteBookResponseDto> res = restTemplate.postForEntity("/users/me/favourite-books", entity,
                FavouriteBookResponseDto.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().bookId()).isEqualTo(bookId);
        assertThat(favouriteBookRepository.findAll()).hasSize(1);
    }

    @Test
    void addFavouriteBook_Duplicate_ShouldReturn409() {
        // first add
        addFavDirect();

        FavouriteBookRequestDto req = new FavouriteBookRequestDto(bookId);
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FavouriteBookRequestDto> entity = new HttpEntity<>(req, userHeaders);

        ResponseEntity<String> res = restTemplate.postForEntity("/users/me/favourite-books", entity, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void addFavouriteBook_BookDoesNotExist_ShouldReturn404() {
        FavouriteBookRequestDto req = new FavouriteBookRequestDto(9_999L);

        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FavouriteBookRequestDto> entity = new HttpEntity<>(req, userHeaders);

        ResponseEntity<String> res = restTemplate.postForEntity("/users/me/favourite-books", entity, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void addFavouriteBook_NullBookId_ShouldReturn400() throws Exception {
        FavouriteBookRequestDto req = new FavouriteBookRequestDto(null);

        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(req), userHeaders);

        ResponseEntity<String> res = restTemplate.postForEntity("/users/me/favourite-books", entity, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // USER
    @Test
    void getMyFavouriteBooks_ShouldReturnOneEntry() {
        addFavDirect();

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        ResponseEntity<Map<String, Object>> res = restTemplate.exchange("/users/me/favourite-books", HttpMethod.GET,
                entity, new ParameterizedTypeReference<>() {
                });

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<?> content = (List<?>) res.getBody().get("content");
        assertThat(content).hasSize(1);
        Map<?, ?> item = (Map<?, ?>) content.get(0);
        assertThat(item.get("bookId")).isEqualTo(bookId.intValue());
    }

    @Test
    void deleteFavouriteBook_ShouldReturn204AndRemove() {
        addFavDirect();

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        ResponseEntity<Void> del = restTemplate.exchange("/users/me/favourite-books/{id}", HttpMethod.DELETE, entity,
                Void.class, bookId);

        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(favouriteBookRepository.findAll()).isEmpty();
    }

    @Test
    void deleteFavouriteBook_NotExists_ShouldReturn404() {
        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        ResponseEntity<String> res = restTemplate.exchange("/users/me/favourite-books/{id}", HttpMethod.DELETE, entity,
                String.class, 8_888L);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ADMIN
    @Test
    void adminGetUserFavouriteBooks_ShouldReturnPage() {
        addFavDirect();

        HttpEntity<Void> entity = new HttpEntity<>(adminHeaders);

        ResponseEntity<Map<String, Object>> res = restTemplate.exchange("/users/{userId}/favourite-books",
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
                }, userId);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<?> content = (List<?>) res.getBody().get("content");
        assertThat(content).hasSize(1);
        Map<?, ?> item = (Map<?, ?>) content.get(0);
        assertThat(item.get("bookId")).isEqualTo(bookId.intValue());
    }

    @Test
    void adminGetUserFavouriteBooks_NoFavourites_ShouldReturnEmptyPage() {
        HttpEntity<Void> entity = new HttpEntity<>(adminHeaders);

        ResponseEntity<Map<String, Object>> res = restTemplate.exchange("/users/{userId}/favourite-books",
                HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
                }, userId);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<?> content = (List<?>) res.getBody().get("content");
        assertThat(content).isEmpty();
    }

    private void addFavDirect() {
        FavouriteBookRequestDto req = new FavouriteBookRequestDto(bookId);
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FavouriteBookRequestDto> entity = new HttpEntity<>(req, userHeaders);

        restTemplate.postForEntity("/users/me/favourite-books", entity, FavouriteBookResponseDto.class);
    }
}
