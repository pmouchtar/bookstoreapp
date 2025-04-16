package com.petros.bookstore.repository;

import com.petros.bookstore.model.Shopping_Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<Shopping_Cart, Long> {
}
