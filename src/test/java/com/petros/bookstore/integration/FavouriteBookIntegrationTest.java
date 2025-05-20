package com.petros.bookstore.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.AbstractPostgresContainerTest;
import com.petros.bookstore.dto.FavouriteBookRequest;
import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.User;
import com.petros.bookstore.model.enums.Role;
import com.petros.bookstore.repository.BookRepository;
import com.petros.bookstore.repository.FavouriteBookRepository;
import com.petros.bookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FavouriteBookIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private FavouriteBookRepository favouriteBookRepository;

    private Long userId;
    private Long adminId;
    private Long bookId;

    private void mockAuthentication(Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        Jwt jwt = new Jwt(
                "fake-token",
                Instant.now(),
                Instant.now().plusSeconds(3_600),
                Map.of("alg", "none"),
                claims
        );

        List<GrantedAuthority> authorities = List.of(() -> "ROLE_" + role);
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, authorities, "user-" + userId);
        auth.setAuthenticated(true);

        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @BeforeEach
    void setUp() {
        // DB reset
        userRepository.deleteAll();
        bookRepository.deleteAll();

        User user = new User();
        user.setFirstName("Petros");
        user.setLastName("Papadopoulos");
        user.setUsername("petrosdev");
        user.setPassword(passwordEncoder.encode("Secure123"));
        user.setRole(Role.USER);
        userId = userRepository.save(user).getId();

        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("Boss");
        admin.setUsername("adminboss");
        admin.setPassword(passwordEncoder.encode("Admin123"));
        admin.setRole(Role.ADMIN);
        adminId = userRepository.save(admin).getId();

        Book book = new Book();
        book.setTitle("Integration Testing 101");
        book.setAuthor("Captain Stub");
        book.setDescription("description");
        book.setPrice(9.99);
        book.setAvailability(10);
        book.setGenre(com.petros.bookstore.model.enums.Genre.SCIENCE_FICTION);
        bookId = bookRepository.save(book).getId();
    }

    @Test
    void addFavouriteBook_ShouldReturn200AndPersist() throws Exception {
        mockAuthentication(userId, "USER");

        FavouriteBookRequest req = new FavouriteBookRequest(bookId);

        mockMvc.perform(post("/users/me/favourite-books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(bookId));

        assertThat(bookRepository.findById(bookId)).isPresent();
    }

    @Test
    void getMyFavouriteBooks_ShouldReturnPageWithOneEntry() throws Exception {
        addFavouriteDirectly();

        mockAuthentication(userId, "USER");

        mockMvc.perform(get("/users/me/favourite-books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].bookId").value(bookId));
    }

    @Test
    void deleteFavouriteBook_ShouldReturn204AndRemove() throws Exception {
        addFavouriteDirectly();

        mockAuthentication(userId, "USER");

        mockMvc.perform(delete("/users/me/favourite-books/{id}", bookId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/me/favourite-books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    //admin get endpoint
    @Test
    void adminGetUserFavouriteBooks_ShouldReturnUserPage() throws Exception {
        addFavouriteDirectly();

        mockAuthentication(adminId, "ADMIN");

        mockMvc.perform(get("/users/{userId}/favourite-books", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].bookId").value(bookId));
    }

    private void addFavouriteDirectly() throws Exception {
        mockAuthentication(userId, "USER");

        FavouriteBookRequest req = new FavouriteBookRequest(bookId);
        mockMvc.perform(post("/users/me/favourite-books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
        SecurityContextHolder.clearContext();
    }

    @Test
    void testAddFavouriteBook_BookDoesNotExist_ShouldReturn404() throws Exception {
        mockAuthentication(userId, "USER");

        FavouriteBookRequest request = new FavouriteBookRequest(9999L);

        mockMvc.perform(post("/users/me/favourite-books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddFavouriteBook_AlreadyExists_ShouldReturn400() throws Exception {
        mockAuthentication(userId, "USER");

        FavouriteBookRequest request = new FavouriteBookRequest(bookId);

        mockMvc.perform(post("/users/me/favourite-books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(bookId));

        mockMvc.perform(post("/users/me/favourite-books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void testAddFavouriteBook_InvalidRequest_ShouldReturn400() throws Exception {
        mockAuthentication(userId, "USER");

        // bookId is null
        FavouriteBookRequest request = new FavouriteBookRequest(null);

        mockMvc.perform(post("/users/me/favourite-books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteFavouriteBook_NotExists_ShouldReturn404() throws Exception {
        mockAuthentication(userId, "USER");

        mockMvc.perform(delete("/users/me/favourite-books/{bookId}", 9999L))
                .andExpect(status().isNotFound());
    }
}
