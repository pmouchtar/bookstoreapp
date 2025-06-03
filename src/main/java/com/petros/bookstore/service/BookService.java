package com.petros.bookstore.service;

import com.petros.bookstore.dto.BookDTO.BookRequestDto;
import com.petros.bookstore.dto.BookDTO.BookResponseDto;
import com.petros.bookstore.dto.BookDTO.BookUpdateRequestDto;
import com.petros.bookstore.exception.customException.ResourceNotFoundException;
import com.petros.bookstore.mapper.BookMapper;
import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.enums.Genre;
import com.petros.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing books. Provides methods for saving, retrieving,
 * updating, deleting, and searching books.
 */
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    /**
     * Saves a new book to the repository.
     *
     * @param request
     *            the book creation request
     * @return the saved book as a response DTO
     */
    public BookResponseDto save(BookRequestDto request) {
        Book savedBook = bookRepository.save(BookMapper.toEntity(request));
        return BookMapper.toResponse(savedBook);
    }

    /**
     * Retrieves all books in a paginated format.
     *
     * @param pageable
     *            pagination information
     * @return a page of book responses
     */
    public Page<BookResponseDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(BookMapper::toResponse);
    }

    /**
     * Retrieves a book by its ID.
     *
     * @param id
     *            the ID of the book
     * @return the book response
     * @throws ResourceNotFoundException
     *             if the book is not found
     */
    public BookResponseDto findBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found."));
        return BookMapper.toResponse(book);
    }

    /**
     * Updates an existing book with the provided data.
     *
     * @param id
     *            the ID of the book to update
     * @param request
     *            the book update request
     * @return the updated book response
     * @throws ResourceNotFoundException
     *             if the book is not found
     */
    public BookResponseDto updateBook(Long id, BookUpdateRequestDto request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found."));

        if (request.title() != null)
            book.setTitle(request.title());
        if (request.author() != null)
            book.setAuthor(request.author());
        if (request.description() != null)
            book.setDescription(request.description());
        if (request.price() != null)
            book.setPrice(request.price());
        if (request.availability() != null)
            book.setAvailability(request.availability());
        if (request.genre() != null)
            book.setGenre(Genre.valueOf(request.genre().toString()));

        return BookMapper.toResponse(bookRepository.save(book));
    }

    /**
     * Deletes a book by its ID.
     *
     * @param id
     *            the ID of the book to delete
     * @return true if the book was deleted
     * @throws ResourceNotFoundException
     *             if the book is not found
     */
    public boolean deleteBookById(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        } else {
            throw new ResourceNotFoundException("Book with ID " + id + " not found.");
        }
    }

    /**
     * Searches for books by various optional filters and pagination.
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
     * @return a page of book responses matching the filters
     */
    public Page<BookResponseDto> searchBooks(String title, String author, Integer availability, Genre genre,
            Double minPrice, Double maxPrice, Pageable pageable) {
        return bookRepository.searchBooks(title, author, genre, availability, minPrice, maxPrice, pageable)
                .map(BookMapper::toResponse);
    }
}
