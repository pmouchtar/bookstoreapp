package com.petros.bookstore.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.AbstractPostgresContainerTest;
import com.petros.bookstore.dto.CartItemRequestDto;
import com.petros.bookstore.dto.CartItemResponseDto;
import com.petros.bookstore.dto.CartItemUpdateRequestDto;
import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.User;
import com.petros.bookstore.model.enums.Genre;
import com.petros.bookstore.model.enums.Role;
import com.petros.bookstore.repository.BookRepository;
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
 * Integration tests for shopping-cart endpoints using TestRestTemplate. Authentication is injected
 * by the DummyJwtFilter through header X-USER-ID.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ShoppingCartIntegrationTest extends AbstractPostgresContainerTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private UserRepository userRepository;
  @Autowired private BookRepository bookRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private ObjectMapper objectMapper;

  private Long userId;
  private Long adminId;
  private Long bookId;

  private HttpHeaders userHeaders;
  private HttpHeaders adminHeaders;

  @BeforeEach
  void setUp() {
    bookRepository.deleteAll();
    userRepository.deleteAll();

    // regular user
    User user = new User();
    user.setFirstName("Petros");
    user.setLastName("Papadopoulos");
    user.setUsername("petrosdev");
    user.setPassword(passwordEncoder.encode("Secure123"));
    user.setRole(Role.USER);
    userId = userRepository.save(user).getId();

    // admin
    User admin = new User();
    admin.setFirstName("Admin");
    admin.setLastName("Boss");
    admin.setUsername("adminboss");
    admin.setPassword(passwordEncoder.encode("Admin123"));
    admin.setRole(Role.ADMIN);
    adminId = userRepository.save(admin).getId();

    // a book
    Book book = new Book();
    book.setTitle("Clean Testing");
    book.setAuthor("Captain Stub");
    book.setDescription("desc");
    book.setPrice(15.50);
    book.setAvailability(20);
    book.setGenre(Genre.SCIENCE_FICTION);
    bookId = bookRepository.save(book).getId();

    userHeaders = new HttpHeaders();
    userHeaders.add("X-USER-ID", userId.toString());

    adminHeaders = new HttpHeaders();
    adminHeaders.add("X-USER-ID", adminId.toString());
  }

  @Test
  void addItemToCart_ShouldReturn200_AndPersist() {
    CartItemRequestDto req = new CartItemRequestDto(bookId, 2);
    userHeaders.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<CartItemRequestDto> entity = new HttpEntity<>(req, userHeaders);

    ResponseEntity<CartItemResponseDto> res =
        restTemplate.postForEntity("/users/me/shopping-cart/items", entity, CartItemResponseDto.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res.getBody()).isNotNull();

    // verify through GET
    Map<String, Object> page = getUserCartPage();
    assertThat(((List<?>) page.get("content"))).hasSize(1);
  }

  @Test
  void addItemToCart_BookDoesNotExist_ShouldReturn404() {
    CartItemRequestDto req = new CartItemRequestDto(9_999L, 1);
    userHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<CartItemRequestDto> entity = new HttpEntity<>(req, userHeaders);

    ResponseEntity<String> res =
        restTemplate.postForEntity("/users/me/shopping-cart/items", entity, String.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void addItemToCart_InvalidQuantity_ShouldReturn400() throws Exception {
    CartItemRequestDto req = new CartItemRequestDto(bookId, 0); // invalid
    userHeaders.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(req), userHeaders);

    ResponseEntity<String> res =
        restTemplate.postForEntity("/users/me/shopping-cart/items", entity, String.class);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void getMyCartItems_ShouldReturnPageWithOneItem() {
    addItemDirect(1);

    Map<String, Object> page = getUserCartPage();

    List<?> content = (List<?>) page.get("content");
    assertThat(content).hasSize(1);
    Map<?, ?> item = (Map<?, ?>) content.get(0);
    assertThat(item.get("quantity")).isEqualTo(1);
  }

  @Test
  void getCartItemById_ShouldReturnItem() {
    Long itemId = addItemDirect(3);

    HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

    ResponseEntity<CartItemResponseDto> res =
        restTemplate.exchange(
            "/users/me/shopping-cart/items/{itemId}",
            HttpMethod.GET,
            entity,
            CartItemResponseDto.class,
            itemId);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res.getBody()).isNotNull();
    assertThat(res.getBody().quantity()).isEqualTo(3);
  }

  @Test
  void updateCartItem_ShouldModifyQuantity() {
    Long itemId = addItemDirect(2);

    CartItemUpdateRequestDto updateReq = new CartItemUpdateRequestDto(5);
    userHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<CartItemUpdateRequestDto> entity = new HttpEntity<>(updateReq, userHeaders);

    ResponseEntity<CartItemResponseDto> res =
        restTemplate.exchange(
            "/users/me/shopping-cart/items/{id}",
            HttpMethod.PUT,
            entity,
            CartItemResponseDto.class,
            itemId);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(res.getBody()).isNotNull();
    assertThat(res.getBody().quantity()).isEqualTo(5);
  }

  @Test
  void updateCartItem_NotFound_ShouldReturn404() {
    CartItemUpdateRequestDto upd = new CartItemUpdateRequestDto(2);
    userHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<CartItemUpdateRequestDto> entity = new HttpEntity<>(upd, userHeaders);

    ResponseEntity<String> res =
        restTemplate.exchange(
            "/users/me/shopping-cart/items/{id}", HttpMethod.PUT, entity, String.class, 9_999L);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void deleteCartItem_ShouldReturn204_AndRemove() {
    Long itemId = addItemDirect(1);

    HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

    ResponseEntity<Void> del =
        restTemplate.exchange(
            "/users/me/shopping-cart/items/{id}", HttpMethod.DELETE, entity, Void.class, itemId);

    assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    // verify empty page
    Map<String, Object> page = getUserCartPage();
    assertThat(((List<?>) page.get("content"))).isEmpty();
  }

  @Test
  void deleteCartItem_NotFound_ShouldReturn404() {
    HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

    ResponseEntity<String> res =
        restTemplate.exchange(
            "/users/me/shopping-cart/items/{id}", HttpMethod.DELETE, entity, String.class, 8_888L);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void adminGetUserCartItems_ShouldReturnPage() {
    addItemDirect(4);

    HttpEntity<Void> entity = new HttpEntity<>(adminHeaders);

    ResponseEntity<Map<String, Object>> res =
        restTemplate.exchange(
            "/users/{userId}/shopping-cart/items",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<>() {},
            userId);

    assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    List<?> content = (List<?>) res.getBody().get("content");
    assertThat(content).hasSize(1);
  }

  private Long addItemDirect(int qty) {
    CartItemRequestDto req = new CartItemRequestDto(bookId, qty);
    userHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<CartItemRequestDto> entity = new HttpEntity<>(req, userHeaders);

    ResponseEntity<CartItemResponseDto> res =
        restTemplate.postForEntity("/users/me/shopping-cart/items", entity, CartItemResponseDto.class);

    return res.getBody().id();
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getUserCartPage() {
    HttpEntity<Void> entity = new HttpEntity<>(userHeaders);
    ResponseEntity<Map<String, Object>> res =
        restTemplate.exchange(
            "/users/me/shopping-cart/items",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<>() {});
    return res.getBody();
  }
}
