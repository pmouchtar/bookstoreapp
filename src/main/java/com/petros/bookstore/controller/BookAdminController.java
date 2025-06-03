package com.petros.bookstore.controller;

import com.petros.bookstore.dto.BookDTO.BookRequestDto;
import com.petros.bookstore.dto.BookDTO.BookResponseDto;
import com.petros.bookstore.dto.BookDTO.BookUpdateRequestDto;
import com.petros.bookstore.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/books")
@PreAuthorize("hasRole('ADMIN')")
public class BookAdminController {

    @Autowired
    private BookService bookService;

    /**
     * Creates a new book.
     *
     * @param bookRequestDto
     *            the book details
     * @return the created book as a response DTO
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public BookResponseDto addBook(@Valid @RequestBody BookRequestDto bookRequestDto) {
        return bookService.save(bookRequestDto);
    }

    /**
     * Updates an existing book by ID.
     *
     * @param bookId
     *            the ID of the book to update
     * @param request
     *            the update request DTO
     * @return the updated book
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponseDto> updateBook(@PathVariable Long bookId,
                                                      @Valid @RequestBody BookUpdateRequestDto request) {
        BookResponseDto updatedBook = bookService.updateBook(bookId, request);
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * Deletes a book by its ID.
     *
     * @param bookId
     *            the ID of the book to delete
     * @return a response with no content
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        boolean deleted = bookService.deleteBookById(bookId);
        return ResponseEntity.noContent().build();
    }
}
