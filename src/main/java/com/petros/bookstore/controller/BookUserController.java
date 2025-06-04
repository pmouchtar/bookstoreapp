package com.petros.bookstore.controller;

import com.petros.bookstore.dto.BookDTO.BookResponseDto;
import com.petros.bookstore.exception.customException.InvalidPriceRangeException;
import com.petros.bookstore.enums.Genre;
import com.petros.bookstore.service.BookService;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing books in the bookstore. Provides endpoints to
 * create, retrieve, update, delete, and search books.
 */
@Validated
@RestController
@RequestMapping("/books")
public class BookUserController {

    @Autowired
    private BookService bookService;

    /**
     * Retrieves books with optional filtering by title, author, availability,
     * genre, and price range.
     *
     * @param title
     *            optional title filter
     * @param author
     *            optional author filter
     * @param availability
     *            optional availability filter
     * @param genre
     *            optional genre filter
     * @param minPrice
     *            optional minimum price
     * @param maxPrice
     *            optional maximum price
     * @param pageable
     *            pagination information
     * @return a page of books matching the filters
     */
    @GetMapping()
    public Page<BookResponseDto> getAllBooks(@RequestParam(required = false) String title,
            @RequestParam(required = false) String author, @RequestParam(required = false) @Min(0) Integer availability,
            @RequestParam(required = false) Genre genre,
            @RequestParam(required = false) @DecimalMin("0.0") @Digits(integer = 5, fraction = 2, message = "decimals up to 2 digits") Double minPrice,
            @RequestParam(required = false) @DecimalMin("0.0") @Digits(integer = 5, fraction = 2, message = "decimals up to 2 digits") Double maxPrice,
            Pageable pageable) {

        if ((minPrice == null) != (maxPrice == null)) {
            throw new InvalidPriceRangeException("Both minPrice and maxPrice should be provided together.");
        }

        if (minPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
        }

        if (title != null || author != null || availability != null || genre != null
                || (minPrice != null & maxPrice != null)) {
            return bookService.searchBooks(title, author, availability, genre, minPrice, maxPrice, pageable);
        } else {
            return bookService.findAll(pageable);
        }
    }

    /**
     * Retrieves a specific book by its ID.
     *
     * @param bookId
     *            the ID of the book
     * @return the book response
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponseDto> getBook(@PathVariable Long bookId) {
        BookResponseDto response = bookService.findBookById(bookId);
        return ResponseEntity.ok(response);
    }
}
