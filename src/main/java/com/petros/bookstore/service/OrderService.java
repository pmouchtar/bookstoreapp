package com.petros.bookstore.service;

import com.petros.bookstore.dto.OrderResponseDto;
import com.petros.bookstore.dto.OrderStatusUpdateRequestDto;
import com.petros.bookstore.exception.ResourceNotFoundException;
import com.petros.bookstore.mapper.OrderMapper;
import com.petros.bookstore.model.*;
import com.petros.bookstore.model.enums.Status;
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

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepo;
    private final ShoppingCartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;

    @Transactional
    public OrderResponseDto placeOrder(Long userId, Pageable pageable) throws BadRequestException {
        User user =
                userRepo
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Shopping_Cart cart =
                cartRepo
                        .findByUser(user)
                        .orElseThrow(() -> new BadRequestException("No cart"));

        Page<Cart_Item> cartItems = cartItemRepo.findByShoppingCart(cart, pageable);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Status.PENDING);
        order.setOrder_date(Timestamp.from(Instant.now()));

        double total = 0.0;
        order.setTotal_price(total);

        order = orderRepo.save(order);
        List<Order_Item> orderItems = new ArrayList<>();

        for (Cart_Item cartItem : cartItems) {
            Order_Item oi = new Order_Item();
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

        cartItemRepo.deleteAll(cartItems); // clear cart


        return OrderMapper.toDto(order);
    }

    @Transactional
    public Page<OrderResponseDto> getOrdersForUser(Long userId, Pageable pageable) {
        User user =
                userRepo
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepo.findByUser(user, pageable).map(OrderMapper::toDto);
    }

    @Transactional
    public OrderResponseDto getOrderForUser(Long orderId, Long userId) {
        User user =
                userRepo
                        .findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Order order =
                orderRepo
                        .findByIdAndUser(orderId, user)
                        .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return OrderMapper.toDto(order);
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatusUpdateRequestDto request) {
        Order order =
                orderRepo
                        .findById(orderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(request.status());
        Order saved = orderRepo.save(order);
        return OrderMapper.toDto(saved);
    }

    public Page<OrderResponseDto> getAllOrders(Pageable pageable) {
        return orderRepo.findAll(pageable).map(OrderMapper::toDto);
    }
}