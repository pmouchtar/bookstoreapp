package com.petros.bookstore.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petros.bookstore.config.AbstractPostgresContainerTest;
import com.petros.bookstore.dto.OrderDTO.OrderResponseDto;
import com.petros.bookstore.dto.OrderDTO.OrderStatusUpdateRequestDto;
import com.petros.bookstore.model.*;
import com.petros.bookstore.enums.Genre;
import com.petros.bookstore.enums.Role;
import com.petros.bookstore.enums.Status;
import com.petros.bookstore.repository.*;
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
 * Integration tests for the Order using the TestDummyJwtFilter.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderIntegrationTest extends AbstractPostgresContainerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long userId;
    private Long adminId;
    private Long bookId;
    private HttpHeaders userHeaders;
    private HttpHeaders adminHeaders;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        cartItemRepository.deleteAll();
        shoppingCartRepository.deleteAll();
        userRepository.deleteAll();
        bookRepository.deleteAll();

        // USER
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

        // BOOK
        Book book = new Book();
        book.setTitle("Integration Testing 101");
        book.setAuthor("Captain Stub");
        book.setDescription("desc");
        book.setPrice(9.99);
        book.setAvailability(10);
        book.setGenre(Genre.SCIENCE_FICTION);
        bookId = bookRepository.save(book).getId();

        Shopping_Cart cart = new Shopping_Cart();
        cart.setUser(user);
        shoppingCartRepository.save(cart);

        Cart_Item item = new Cart_Item();
        item.setBook(book);
        item.setShoppingCart(cart);
        item.setQuantity(2);
        cartItemRepository.save(item);

        // Dummy‑JWT headers
        userHeaders = new HttpHeaders();
        userHeaders.add("X-USER-ID", userId.toString());

        adminHeaders = new HttpHeaders();
        adminHeaders.add("X-USER-ID", adminId.toString());
    }

    @Test
    void placeOrder_ShouldReturn200_AndPersistOrder() {
        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        ResponseEntity<OrderResponseDto> res = restTemplate.postForEntity("/users/me/orders", entity,
                OrderResponseDto.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(orderRepository.findAll()).hasSize(1);
    }

    @Test
    void myOrders_ShouldReturnPageWithOneOrder() {
        // first place an order
        placeOrderViaEndpoint();

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        ResponseEntity<Map<String, Object>> res = restTemplate.exchange("/users/me/orders", HttpMethod.GET, entity,
                new ParameterizedTypeReference<>() {
                });

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<?> content = (List<?>) res.getBody().get("content");
        assertThat(content).hasSize(1);
    }

    @Test
    void getSingleOrder_ShouldReturnOrder() {
        Long orderId = placeOrderViaEndpoint();

        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);
        ResponseEntity<OrderResponseDto> res = restTemplate.exchange("/users/me/orders/{orderId}", HttpMethod.GET,
                entity, OrderResponseDto.class, orderId);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().id()).isEqualTo(orderId);
    }

    @Test
    void adminGetAllOrders_ShouldReturnPage() {
        placeOrderViaEndpoint();

        HttpEntity<Void> entity = new HttpEntity<>(adminHeaders);

        ResponseEntity<Map<String, Object>> res = restTemplate.exchange("/orders", HttpMethod.GET, entity,
                new ParameterizedTypeReference<>() {
                });

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<?> content = (List<?>) res.getBody().get("content");
        assertThat(content).hasSize(1);
    }

    @Test
    void adminUpdateOrderStatus_ShouldReturnUpdatedStatus() {
        Long orderId = placeOrderViaEndpoint();

        OrderStatusUpdateRequestDto req = new OrderStatusUpdateRequestDto(Status.SHIPPED);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderStatusUpdateRequestDto> entity = new HttpEntity<>(req, adminHeaders);

        ResponseEntity<OrderResponseDto> res = restTemplate.exchange("/orders/{orderId}", HttpMethod.PUT, entity,
                OrderResponseDto.class, orderId);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().status()).isEqualTo(Status.SHIPPED);
    }

    @Test
    void adminGetOrderById_ShouldReturnOrder() {
        Long orderId = placeOrderViaEndpoint();

        HttpEntity<Void> entity = new HttpEntity<>(adminHeaders);

        ResponseEntity<OrderResponseDto> res = restTemplate.exchange("/orders/{orderId}", HttpMethod.GET, entity,
                OrderResponseDto.class, orderId);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().id()).isEqualTo(orderId);
    }

    // place order util method
    private Long placeOrderViaEndpoint() {
        HttpEntity<Void> entity = new HttpEntity<>(userHeaders);

        ResponseEntity<OrderResponseDto> res = restTemplate.postForEntity("/users/me/orders", entity,
                OrderResponseDto.class);
        return res.getBody().id();
    }
}
