package com.petros.bookstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.petros.bookstore.model.enums.Genre;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Float price;

    @Column(nullable = false)
    private int availability;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;

    public Book(String title, String author, String description, float price, int availability, Genre genre) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.availability = availability;
        this.genre = genre;
    }
}
