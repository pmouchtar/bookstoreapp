package com.petros.bookstore.service;

import com.petros.bookstore.dto.cartitemdto.CartItemRequestDto;
import com.petros.bookstore.dto.cartitemdto.CartItemResponseDto;
import com.petros.bookstore.dto.cartitemdto.CartItemUpdateRequestDto;
import com.petros.bookstore.exception.customException.ResourceNotFoundException;
import com.petros.bookstore.mapper.CartItemMapper;
import com.petros.bookstore.model.*;
import com.petros.bookstore.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing the shopping cart functionality
 * including adding items, retrieving cart contents, updating item quantities,
 * and removing items.
 */
@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;

    /**
     * Adds a book to the user's shopping cart. If the cart or the item doesn't
     * exist, it will be created. If the item already exists in the cart, its
     * quantity is updated.
     *
     * @param userId
     *            ID of the user
     * @param request
     *            DTO containing the book ID and desired quantity
     * @return DTO representing the added or updated cart item
     * @throws ResourceNotFoundException
     *             if user or book is not found
     */
    @Transactional
    public CartItemResponseDto addToCart(Long userId, CartItemRequestDto request) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(//
                "User not found"));

        Book book = bookRepo.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        ShoppingCart cart = cartRepo.findByUser(user).orElseGet(() -> {
            ShoppingCart c = new ShoppingCart();
            c.setUser(user);
            return cartRepo.save(c);
        });

        CartItem item = itemRepo.findByShoppingCartAndBook(cart, book).orElse(null);
        if (item == null) {
            item = new CartItem();
            item.setShoppingCart(cart);
            item.setBook(book);
            item.setQuantity(request.quantity());
        } else {
            item.setQuantity(item.getQuantity() + request.quantity());
        }

        CartItem saved = itemRepo.save(item);
        return CartItemMapper.toDto(saved);
    }

    /**
     * Retrieves a paginated list of items in the user's shopping cart.
     *
     * @param userId
     *            ID of the user
     * @param pageable
     *            pagination information
     * @return a page of cart item DTOs
     * @throws ResourceNotFoundException
     *             if user is not found
     */
    @Transactional
    public Page<CartItemResponseDto> getCartItems(Long userId, Pageable pageable) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(//
                "User not found"));

        ShoppingCart cart = cartRepo.findByUser(user).orElse(null);

        if (cart == null) {
            return Page.empty(pageable);
        }

        return itemRepo.findByShoppingCart(cart, pageable).map(CartItemMapper::toDto);
    }

    /**
     * Retrieves a single cart item by its ID, ensuring it belongs to the specified
     * user.
     *
     * @param itemId
     *            ID of the cart item
     * @param userId
     *            ID of the user
     * @return DTO of the found cart item
     * @throws ResourceNotFoundException
     *             if the item is not found or does not belong to the user
     */
    @Transactional
    public CartItemResponseDto findItemById(Long itemId, Long userId) {
        CartItem item = itemRepo.findById(itemId).filter(//
                i -> i.getShoppingCart().getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        return CartItemMapper.toDto(item);
    }

    /**
     * Updates the quantity of a cart item. If the new quantity is 0, the item is
     * removed from the cart.
     *
     * @param itemId
     *            ID of the cart item to update
     * @param request
     *            DTO containing the new quantity
     * @param userId
     *            ID of the user
     * @return DTO of the updated cart item, or null if the item was deleted
     * @throws ResourceNotFoundException
     *             if the item is not found or does not belong to the user
     */
    @Transactional
    public CartItemResponseDto updateCartItem(//
            Long itemId, CartItemUpdateRequestDto request, Long userId) {
        CartItem item = itemRepo.findById(itemId).filter(//
                i -> i.getShoppingCart().getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        int newQty = request.quantity();
        if (newQty == 0) {
            itemRepo.delete(item);
            return null;
        }
        item.setQuantity(newQty);
        CartItem saved = itemRepo.save(item);
        return CartItemMapper.toDto(saved);
    }

    /**
     * Removes a cart item from the user's shopping cart.
     *
     * @param userId
     *            ID of the user
     * @param itemId
     *            ID of the cart item to remove
     * @throws ResourceNotFoundException
     *             if the item is not found or does not belong to the user
     */
    @Transactional
    public void removeFromCart(Long userId, Long itemId) {
        CartItem item = itemRepo.findById(itemId).filter(//
                i -> i.getShoppingCart().getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        itemRepo.delete(item);
    }
}
