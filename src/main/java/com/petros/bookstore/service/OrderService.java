package com.petros.bookstore.service;

import com.petros.bookstore.dto.OrderDTO.OrderResponseDto;
import com.petros.bookstore.dto.OrderDTO.OrderStatusUpdateRequestDto;
import com.petros.bookstore.enums.Status;
import com.petros.bookstore.exception.customException.ResourceNotFoundException;
import com.petros.bookstore.mapper.OrderMapper;
import com.petros.bookstore.model.*;
import com.petros.bookstore.repository.*;
import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling operations related to placing and managing
 * orders.
 * Includes functionality for placing orders, retrieving orders by user or
 * globally (admin), and updating order status.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepo;
    private final ShoppingCartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;

    /**
     * Places an order for the specified user by transferring items from the user's
     * cart into a new order.
     *
     * @param userId
     *            the ID of the user placing the order
     * @param pageable
     *            the pagination object for cart item retrieval
     * @return the DTO representation of the placed order
     * @throws BadRequestException
     *             if the cart does not exist or is empty
     */
    @Transactional
    public OrderResponseDto placeOrder(Long userId, Pageable pageable) throws BadRequestException {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(//
                "User not found"));

        ShoppingCart cart = cartRepo.findByUser(user).orElseThrow(() -> new BadRequestException(//
                "No cart"));

        Page<CartItem> cartItems = cartItemRepo.findByShoppingCart(cart, pageable);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Status.PENDING);
        order.setOrder_date(Timestamp.from(Instant.now()));
        order.setTotal_price(0.0);

        order = orderRepo.save(order);
        List<OrderItem> orderItems = new ArrayList<>();

        double total = 0.0;

        for (CartItem cartItem : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setBook(cartItem.getBook());
            oi.setQuantity(cartItem.getQuantity());
            orderItems.add(oi);
            total += cartItem.getBook().getPrice() * cartItem.getQuantity();
            orderItemRepo.save(oi);
        }

        order.setTotal_price(total);
        order.setOrderItems(orderItems);
        order = orderRepo.save(order);

        cartItemRepo.deleteAll(cartItems); // clear cart after placing order

        return OrderMapper.toDto(order);
    }

    /**
     * Retrieves all orders placed by the specified user.
     *
     * @param userId
     *            the user's ID
     * @param pageable
     *            pagination parameters
     * @return paginated list of order response DTOs
     */
    @Transactional
    public Page<OrderResponseDto> getOrdersForUser(Long userId, Pageable pageable) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(//
                "User not found"));

        return orderRepo.findByUser(user, pageable).map(OrderMapper::toDto);
    }

    /**
     * Retrieves a specific order for the given user.
     *
     * @param orderId
     *            the ID of the order
     * @param userId
     *            the ID of the user
     * @return the order response DTO
     */
    @Transactional
    public OrderResponseDto getOrderForUser(Long orderId, Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(//
                "User not found"));

        Order order = orderRepo.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return OrderMapper.toDto(order);
    }

    /**
     * Updates the status of an order.
     *
     * @param orderId
     *            the ID of the order
     * @param request
     *            the DTO containing the new status
     * @return the updated order as a response DTO
     */
    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatusUpdateRequestDto request) {
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new ResourceNotFoundException(//
                "Order not found"));

        order.setStatus(request.status());
        Order saved = orderRepo.save(order);

        return OrderMapper.toDto(saved);
    }

    /**
     * Retrieves all orders in the system (admin view).
     *
     * @param pageable
     *            pagination information
     * @return paginated list of all orders as DTOs
     */
    public Page<OrderResponseDto> getAllOrders(Pageable pageable) {
        return orderRepo.findAll(pageable).map(OrderMapper::toDto);
    }

    /**
     * Retrieves a specific order by ID.
     *
     * @param orderId
     *            the ID of the order
     * @return the corresponding order as a DTO
     */
    public OrderResponseDto getOrderById(Long orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new ResourceNotFoundException(//
                "Order not found"));

        return OrderMapper.toDto(order);
    }
}
