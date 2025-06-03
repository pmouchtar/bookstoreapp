package com.petros.bookstore.repository;

import com.petros.bookstore.model.Shopping_Cart;
import com.petros.bookstore.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<Shopping_Cart, Long> {
    Optional<Shopping_Cart> findByUser(User user);
}
