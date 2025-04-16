package com.petros.bookstore.repository;

import com.petros.bookstore.model.Cart_Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<Cart_Item, Long> {
}
