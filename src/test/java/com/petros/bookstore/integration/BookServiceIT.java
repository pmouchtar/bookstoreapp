package com.petros.bookstore.integration;

import static org.assertj.core.api.Assertions.*;

import com.petros.bookstore.config.AbstractPostgresContainerTest;
import com.petros.bookstore.dto.BookRequestDto;
import com.petros.bookstore.dto.BookResponseDto;
import com.petros.bookstore.dto.BookUpdateRequestDto;
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

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookServiceIT extends AbstractPostgresContainerTest {

  @Autowired private BookService bookService;

  @Autowired private BookRepository bookRepository;

  private BookRequestDto bookRequestDto;
  private BookRequestDto bookRequestDto2;

  @BeforeEach
  void setUp() {
    bookRepository.deleteAll();

    bookRequestDto =
        new BookRequestDto("Simple Book", "John Doe", "simple description", 9.99, 100, Genre.DRAMA);
    bookService.save(bookRequestDto);
    bookRequestDto2 =
        new BookRequestDto(
            "Another Book", "Jane Doe", "another description", 19.99, 50, Genre.SCIENCE_FICTION);
    bookService.save(bookRequestDto2);
  }

  @Test
  void testSaveAndFindById() {
    BookResponseDto saved = bookService.save(bookRequestDto);
    BookResponseDto found = bookService.findBookById(saved.getId());

    assertThat(found.getTitle()).isEqualTo("Simple Book");
    assertThat(found.getAuthor()).isEqualTo("John Doe");
    assertThat(found.getAvailability()).isEqualTo(100);
  }

  @Test
  void testFindAll() {
    bookService.save(bookRequestDto);
    Page<BookResponseDto> page = bookService.findAll(PageRequest.of(0, 10));

    assertThat(page.getContent()).isNotEmpty();
    assertThat(page.getContent().get(0).getTitle()).isEqualTo("Simple Book");
    assertThat(page.getContent()).hasSize(3);
  }

  @Test
  void testUpdateBook() {
    BookResponseDto saved = bookService.save(bookRequestDto);
    BookUpdateRequestDto update = new BookUpdateRequestDto();
    update.setTitle("Updated Simple Book");
    update.setAvailability(50);

    BookResponseDto updated = bookService.updateBook(saved.getId(), update);

    assertThat(updated.getTitle()).isEqualTo("Updated Simple Book");
    assertThat(updated.getAvailability()).isEqualTo(50);
  }

  @Test
  void testDeleteBook() {
    BookResponseDto saved = bookService.save(bookRequestDto);
    boolean deleted = bookService.deleteBookById(saved.getId());

    assertThat(deleted).isTrue();
    assertThatThrownBy(() -> bookService.findBookById(saved.getId()))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  void testSearchBooks() {
    Page<BookResponseDto> results =
        bookService.searchBooks(
            "Simple", "John", 100, Genre.DRAMA, 5.0, 15.0, PageRequest.of(0, 10));

    assertThat(results.getContent()).hasSize(1);
    assertThat(results.getContent().get(0).getTitle()).contains("Simple");
  }
}
