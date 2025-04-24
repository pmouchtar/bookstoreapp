package com.petros.bookstore.repository;

import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.enums.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BookRepositoryTest {

    private BookRepository bookRepository;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);

        book1 = new Book(1L, "Dune", "Frank Herbert", "Sci-fi epic", 19.99f, 10, Genre.SCIENCE_FICTION);
        book2 = new Book(2L, "The Hobbit", "J.R.R. Tolkien", "Fantasy novel", 10.99f, 5, Genre.FANTASY);
    }

    @Test
    void testFindAll() {
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<Book> books = bookRepository.findAll();

        assertThat(books).hasSize(2);
        verify(bookRepository).findAll();
    }

    @Test
    void testFindAllWithPageable() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Book> bookPage = new PageImpl<>(List.of(book1, book2));
        when(bookRepository.findAll(pageRequest)).thenReturn(bookPage);

        Page<Book> result = bookRepository.findAll(pageRequest);

        assertThat(result.getContent()).hasSize(2);
        verify(bookRepository).findAll(pageRequest);
    }

    @Test
    void testFindById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        Optional<Book> result = bookRepository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Dune");
        verify(bookRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(bookRepository.findById(3L)).thenReturn(Optional.empty());

        Optional<Book> result = bookRepository.findById(3L);

        assertThat(result).isNotPresent();
        verify(bookRepository).findById(3L);
    }

    @Test
    void testSave() {
        Book newBook = new Book(null, "1984", "George Orwell", "Dystopian novel", 15.0f, 8, Genre.SCIENCE_FICTION);
        when(bookRepository.save(newBook)).thenReturn(book1);

        Book saved = bookRepository.save(newBook);

        assertThat(saved).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Dune");
        verify(bookRepository).save(newBook);
    }

    @Test
    void testDeleteById() {
        doNothing().when(bookRepository).deleteById(1L);

        bookRepository.deleteById(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    void testExistsByIdTrue() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        boolean exists = bookRepository.existsById(1L);

        assertThat(exists).isTrue();
        verify(bookRepository).existsById(1L);
    }

    @Test
    void testExistsByIdFalse() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        boolean exists = bookRepository.existsById(99L);

        assertThat(exists).isFalse();
        verify(bookRepository).existsById(99L);
    }

    @Test
    void testSearchBooksByTitle() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(book1));

        when(bookRepository.searchBooks(
                eq("Dune"), isNull(), isNull(), isNull(), isNull(), isNull(), eq(pageable))
        ).thenReturn(page);

        Page<Book> result = bookRepository.searchBooks("Dune", null, null, null, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAuthor()).isEqualTo("Frank Herbert");
        verify(bookRepository).searchBooks("Dune", null, null, null, null, null, pageable);
    }
}