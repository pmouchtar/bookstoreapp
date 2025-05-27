package com.petros.bookstore.service;

import com.petros.bookstore.dto.CartItemRequestDto;
import com.petros.bookstore.dto.CartItemResponseDto;
import com.petros.bookstore.dto.CartItemUpdateRequestDto;
import com.petros.bookstore.exception.ResourceNotFoundException;
import com.petros.bookstore.mapper.CartItemMapper;
import com.petros.bookstore.model.*;
import com.petros.bookstore.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

  private final ShoppingCartRepository cartRepo;
  private final CartItemRepository itemRepo;
  private final BookRepository bookRepo;
  private final UserRepository userRepo;

  @Transactional
  public CartItemResponseDto addToCart(Long userId, CartItemRequestDto request) {
    User user =
        userRepo
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Book book =
        bookRepo
            .findById(request.getBookId())
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    Shopping_Cart cart =
        cartRepo
            .findByUser(user)
            .orElseGet(
                () -> { // if, for some reason, user's cart doesn't exist we create it here in the
                  // first add to his cart
                  Shopping_Cart c = new Shopping_Cart();
                  c.setUser(user);
                  return cartRepo.save(c);
                });

    Cart_Item item = itemRepo.findByShoppingCartAndBook(cart, book).orElse(null);
    if (item == null) {
      item = new Cart_Item();
      item.setShoppingCart(cart);
      item.setBook(book);
      item.setQuantity(request.getQuantity());
    } else {
      item.setQuantity(item.getQuantity() + request.getQuantity());
    }

    Cart_Item saved = itemRepo.save(item);
    return CartItemMapper.toDto(saved);
  }

  @Transactional
  public Page<CartItemResponseDto> getCartItems(Long userId, Pageable pageable) {
    User user =
        userRepo
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Shopping_Cart cart = cartRepo.findByUser(user).orElse(null);

    if (cart == null) {
      return Page.empty(pageable);
    }

    return itemRepo.findByShoppingCart(cart, pageable).map(CartItemMapper::toDto);
  }

  @Transactional
  public CartItemResponseDto findItemById(Long itemId, Long userId) {
    Cart_Item item =
        itemRepo
            .findById(itemId)
            .filter(i -> i.getShoppingCart().getUser().getId().equals(userId))
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

    return CartItemMapper.toDto(item);
  }

  @Transactional
  public CartItemResponseDto updateCartItem(Long itemId, CartItemUpdateRequestDto request, Long userId) {
    Cart_Item item =
        itemRepo
            .findById(itemId)
            .filter(i -> i.getShoppingCart().getUser().getId().equals(userId))
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

    int newQty = request.getQuantity();
    if (newQty <= 0) {
      itemRepo.delete(item);
      return null;
    }
    item.setQuantity(newQty);
    Cart_Item saved = itemRepo.save(item);
    return CartItemMapper.toDto(saved);
  }

  @Transactional
  public void removeFromCart(Long userId, Long itemId) {
    Cart_Item item =
        itemRepo
            .findById(itemId)
            .filter(i -> i.getShoppingCart().getUser().getId().equals(userId))
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
    itemRepo.delete(item);
  }
}
