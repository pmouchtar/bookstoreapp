package com.petros.bookstore.controller;

import com.petros.bookstore.dto.BookRequest;
import com.petros.bookstore.dto.BookResponse;
import com.petros.bookstore.model.Book;
import com.petros.bookstore.repository.BookRepository;
import com.petros.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping()
    public BookResponse addBook(@RequestBody BookRequest bookRequest) {
        return bookService.save(bookRequest);
    }

    @GetMapping()
    public List<BookResponse> getAllBooks(
        @RequestParam (required = false) String title,
        @RequestParam (required = false) String author,
        @RequestParam (required = false) String genre,
        @RequestParam (required = false) Float minPrice,
        @RequestParam (required = false) Float maxPrice)
        {
            if (title != null || author != null || (minPrice != null && maxPrice != null)) {
                return bookService.searchBooks(title, author, genre, minPrice, maxPrice);
            } else {
                return bookService.findAll();
                }
        }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long bookId) {
        return bookService.findBookById(bookId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long bookId, @RequestBody BookRequest bookRequest) {
        Optional<BookResponse> updated = bookService.updateBook(bookId, bookRequest);
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
