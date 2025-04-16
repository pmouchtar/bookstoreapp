package com.petros.bookstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart_items",uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "shopping_cart_id"}))
public class Cart_Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    private Shopping_Cart shoppingCart;

    @Column(nullable = false)
    private int quantity;
}
