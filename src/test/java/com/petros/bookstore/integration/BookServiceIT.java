package com.petros.bookstore.integration;

import com.petros.bookstore.dto.BookRequest;
import com.petros.bookstore.dto.BookResponse;
import com.petros.bookstore.dto.BookUpdateRequest;
import com.petros.bookstore.model.enums.Genre;
import com.petros.bookstore.repository.BookRepository;
import com.petros.bookstore.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookServiceIT {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    private BookRequest bookRequest;
    private BookRequest bookRequest2;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();

        bookRequest = new BookRequest("Simple Book", "John Doe", "simple description", 9.99f, 100, Genre.DRAMA);
        bookService.save(bookRequest);
        bookRequest2 = new BookRequest("Another Book", "Jane Doe", "another description", 19.99f, 50, Genre.SCIENCE_FICTION);
        bookService.save(bookRequest2);
    }

    @Test
    void testSaveAndFindById() {
        BookResponse saved = bookService.save(bookRequest);
        BookResponse found = bookService.findBookById(saved.getId());

        assertThat(found.getTitle()).isEqualTo("Simple Book");
        assertThat(found.getAuthor()).isEqualTo("John Doe");
        assertThat(found.getAvailability()).isEqualTo(100);
    }

    @Test
    void testFindAll() {
        bookService.save(bookRequest);
        Page<BookResponse> page = bookService.findAll(PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Simple Book");
        assertThat(page.getContent()).hasSize(3);
    }

    @Test
    void testUpdateBook() {
        BookResponse saved = bookService.save(bookRequest);
        BookUpdateRequest update = new BookUpdateRequest();
        update.setTitle("Updated Simple Book");
        update.setAvailability(50);

        BookResponse updated = bookService.updateBook(saved.getId(), update);

        assertThat(updated.getTitle()).isEqualTo("Updated Simple Book");
        assertThat(updated.getAvailability()).isEqualTo(50);
    }

    @Test
    void testDeleteBook() {
        BookResponse saved = bookService.save(bookRequest);
        boolean deleted = bookService.deleteBookById(saved.getId());

        assertThat(deleted).isTrue();
        assertThatThrownBy(() -> bookService.findBookById(saved.getId()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testSearchBooks() {
        //bookService.save(bookRequest);
        Page<BookResponse> results = bookService.searchBooks(
                "Simple", "John", 100, Genre.DRAMA, 5.0f, 15.0f, PageRequest.of(0, 10));

        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getTitle()).contains("Simple");
    }
}
