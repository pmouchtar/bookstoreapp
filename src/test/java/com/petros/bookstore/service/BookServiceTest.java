package com.petros.bookstore.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.petros.bookstore.dto.BookRequest;
import com.petros.bookstore.dto.BookResponse;
import com.petros.bookstore.dto.BookUpdateRequest;
import com.petros.bookstore.exception.ResourceNotFoundException;
import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.enums.Genre;
import com.petros.bookstore.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class BookServiceTest {

  @Mock private BookRepository bookRepository;

  @InjectMocks private BookService bookService;

  private Book book;
  private BookRequest bookRequest;
  private BookUpdateRequest bookUpdateRequest;

  @BeforeEach
  void setup() {
    book = new Book(1L, "Title", "Author", "Description", 19.99, 10, Genre.FANTASY);
    bookRequest = new BookRequest("Title", "Author", "Description", 19.99, 10, Genre.FANTASY);
    bookUpdateRequest = new BookUpdateRequest("New Title", null, null, null, null, null);
  }

  @Test
  void testSave() {
    when(bookRepository.save(any(Book.class))).thenReturn(book);

    BookResponse response = bookService.save(bookRequest);

    assertThat(response).isNotNull();
    assertThat(response.getTitle()).isEqualTo("Title");

    verify(bookRepository).save(any(Book.class));
  }

  @Test
  void testFindAll() {
    Page<Book> page = new PageImpl<>(List.of(book));
    when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);

    Page<BookResponse> result = bookService.findAll(PageRequest.of(0, 10));

    assertThat(result).hasSize(1);
    verify(bookRepository).findAll(any(Pageable.class));
  }

  @Test
  void testFindBookByIdFound() {
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

    BookResponse response = bookService.findBookById(1L);

    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getTitle()).isEqualTo("Title");
  }

  @Test
  void testFindBookByIdNotFound() {
    when(bookRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookService.findBookById(999L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Book with ID 999 not found.");
  }

  @Test
  void testUpdateBook() {
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(bookRepository.save(any(Book.class))).thenReturn(book);

    BookResponse response = bookService.updateBook(1L, bookUpdateRequest);

    assertThat(response.getTitle()).isEqualTo("New Title");
    verify(bookRepository).save(any(Book.class));
  }

  @Test
  void testUpdateBookNotFound() {
    when(bookRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookService.updateBook(1L, bookUpdateRequest))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Book with ID 1 not found.");
  }

  @Test
  void testDeleteBookByIdSuccess() {
    when(bookRepository.existsById(1L)).thenReturn(true);

    boolean result = bookService.deleteBookById(1L);

    assertThat(result).isTrue();
    verify(bookRepository).deleteById(1L);
  }

  @Test
  void testDeleteBookByIdNotFound() {
    when(bookRepository.existsById(1L)).thenReturn(false);

    assertThatThrownBy(() -> bookService.deleteBookById(1L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Book with ID 1 not found.");
  }

  @Test
  void testSearchBooks() {
    Page<Book> page = new PageImpl<>(List.of(book));
    when(bookRepository.searchBooks(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(page);

    Page<BookResponse> result =
        bookService.searchBooks(
            "Title", null, null, Genre.FANTASY, null, null, PageRequest.of(0, 10));

    assertThat(result.getContent()).hasSize(1);
    verify(bookRepository).searchBooks(any(), any(), any(), any(), any(), any(), any());
  }
}
