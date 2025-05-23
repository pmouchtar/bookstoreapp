package com.petros.bookstore.repository;

import com.petros.bookstore.model.Order_Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<Order_Item, Long> {}
