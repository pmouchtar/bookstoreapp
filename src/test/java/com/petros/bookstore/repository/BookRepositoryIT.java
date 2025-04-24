package com.petros.bookstore.repository;

import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.enums.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryIT {

    @Autowired
    private BookRepository bookRepository;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();

        book1 = new Book();
        book1.setTitle("Spring in Action");
        book1.setAuthor("Craig Walls");
        book1.setPrice(45.0f);
        book1.setAvailability(5);
        book1.setGenre(Genre.TECH);
        book1.setDescription("A comprehensive Spring book");

        book2 = new Book();
        book2.setTitle("Java Fundamentals");
        book2.setAuthor("John Doe");
        book2.setPrice(35.0f);
        book2.setAvailability(10);
        book2.setGenre(Genre.TECH);
        book2.setDescription("Basics of Java");

        bookRepository.saveAll(List.of(book1, book2));
    }

    @Test
    void testFindAllBooks() {
        List<Book> books = bookRepository.findAll();
        assertThat(books).hasSize(2);
    }

    @Test
    void testSearchBooksByTitle() {
        Page<Book> result = bookRepository.searchBooks(
                "Spring", null, null, null, null, null, PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).contains("Spring");
        assertThat(result.getContent().get(0).getAuthor()).contains("Craig");
        assertThat(result.getContent().get(0).getPrice()).isEqualTo(45.0f);
    }

    @Test
    void testSearchBooksWithPriceRange() {
        Page<Book> result = bookRepository.searchBooks(
                null, null, null, null, 40.0f, 50.0f, PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPrice()).isGreaterThanOrEqualTo(40.0f);
    }

    @Test
    void testSearchBooksWithMultipleFilters() {
        Page<Book> result = bookRepository.searchBooks(
                "Java", "John Doe", Genre.TECH, 5, 30.0f, 40.0f, PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).contains("Java");
    }

    @Test
    void testSearchBooksWithAllNullFiltersReturnsAll() {
        Page<Book> result = bookRepository.searchBooks(
                null, null, null, null, null, null, PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void testSearchBooksWithNoMatchReturnsEmpty() {
        Page<Book> result = bookRepository.searchBooks(
                "Nonexistent", "Nobody", Genre.HISTORY, 100, 100.0f, 200.0f, PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void testSearchBooksByGenre() {
        Page<Book> result = bookRepository.searchBooks(
                null, null, Genre.TECH, null, null, null, PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(book -> book.getGenre() == Genre.TECH);
    }

    @Test
    void testSearchBooksPagination() {
        // Page 0, size 1
        Page<Book> page1 = bookRepository.searchBooks(
                null, null, null, null, null, null, PageRequest.of(0, 1)
        );

        // Page 1, size 1
        Page<Book> page2 = bookRepository.searchBooks(
                null, null, null, null, null, null, PageRequest.of(1, 1)
        );

        assertThat(page1.getContent()).hasSize(1);
        assertThat(page2.getContent()).hasSize(1);
        assertThat(page1.getContent().get(0)).isNotEqualTo(page2.getContent().get(0));
    }
}
