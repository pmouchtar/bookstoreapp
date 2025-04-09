package com.petros.bookstore.model;

import jakarta.persistence.*;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    // Constructors
    public Book() {}

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }


}
