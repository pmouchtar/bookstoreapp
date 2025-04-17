package com.petros.bookstore.controller;

import com.petros.bookstore.dto.BookRequest;
import com.petros.bookstore.dto.BookResponse;
import com.petros.bookstore.dto.BookUpdateRequest;
import com.petros.bookstore.exception.InvalidPriceRangeException;
import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.enums.Genre;
import com.petros.bookstore.repository.BookRepository;
import com.petros.bookstore.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping()
    public BookResponse addBook(@Valid @RequestBody BookRequest bookRequest) {
        return bookService.save(bookRequest);
    }

    @GetMapping()
    public Page<BookResponse> getAllBooks(
        @RequestParam (required = false) String title,
        @RequestParam (required = false) String author,
        @RequestParam (required = false) Integer availability,
        @RequestParam (required = false) String genre,
        @RequestParam (required = false) Float minPrice,
        @RequestParam (required = false) Float maxPrice,
        Pageable pageable)
        {
            Genre genreEnum = null;
            if (genre != null) {
                try {
                    genreEnum = Genre.valueOf(genre.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid genre: " + genre);
                }
            }
            if ((minPrice == null) != (maxPrice == null)) {
                throw new InvalidPriceRangeException("Both minPrice and maxPrice should be provided together.");
            }
            if (title != null || author != null || availability != null || genreEnum != null || (minPrice != null & maxPrice != null)) {
                return bookService.searchBooks(title, author, availability, genreEnum, minPrice, maxPrice, pageable);
            } else {
                return bookService.findAll(pageable);
                }
        }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long bookId) {
        BookResponse response = bookService.findBookById(bookId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long bookId,@Valid @RequestBody BookUpdateRequest request) {
        Optional<BookResponse> updated = bookService.updateBook(bookId, request);
        return updated.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.deleteBookById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
