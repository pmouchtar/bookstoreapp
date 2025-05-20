package com.petros.bookstore.repository;

import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.Cart_Item;
import com.petros.bookstore.model.Shopping_Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<Cart_Item, Long> {
    Optional<Cart_Item> findByShoppingCartAndBook(Shopping_Cart cart, Book book);

    Page<Cart_Item> findByShoppingCart(Shopping_Cart shoppingCart, Pageable pageable);
}
