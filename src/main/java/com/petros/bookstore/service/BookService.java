package com.petros.bookstore.service;

import com.petros.bookstore.dto.BookRequest;
import com.petros.bookstore.dto.BookResponse;
import com.petros.bookstore.dto.BookUpdateRequest;
import com.petros.bookstore.exception.ResourceNotFoundException;
import com.petros.bookstore.mapper.BookMapper;
import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.enums.Genre;
import com.petros.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public BookResponse save(BookRequest request) {
        Book savedBook = bookRepository.save(BookMapper.toEntity(request));
        return BookMapper.toResponse(savedBook);
    }

    public Page<BookResponse> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(BookMapper::toResponse);
    }

    public BookResponse findBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + id + " not found"));
        return BookMapper.toResponse(book);
    }

    public Optional<BookResponse> updateBook(Long id, BookUpdateRequest request) {
        return bookRepository.findById(id).map(book -> {
            if (request.getTitle() != null) book.setTitle(request.getTitle());
            if (request.getAuthor() != null) book.setAuthor(request.getAuthor());
            if (request.getDescription() != null) book.setDescription(request.getDescription());
            if (request.getPrice() != null) book.setPrice(request.getPrice());
            if (request.getAvailability() != null) book.setAvailability(request.getAvailability());
            if (request.getGenre() != null) book.setGenre(Genre.valueOf(request.getGenre().toString()));
            return BookMapper.toResponse(bookRepository.save(book));
        });
    }

    public boolean deleteBookById(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

//    public List<BookResponse> searchBooks(String title, String author, Integer availability, String genre, Float minPrice, Float maxPrice) {
//        return bookRepository.findAll().stream()
//                .filter(book -> (title == null || book.getTitle().toLowerCase().contains(title.toLowerCase())) &&
//                        (author == null || book.getAuthor().toLowerCase().contains(author.toLowerCase())) &&
//                        (genre == null || book.getGenre().name().equalsIgnoreCase(genre)) &&
//                        (availability == null || book.getAvailability() >= availability) &&
//                        (minPrice == null || book.getPrice() >= minPrice) &&
//                        (maxPrice == null || book.getPrice() <= maxPrice))
//                .map(BookMapper::toResponse)
//                .toList();
//    }
    public Page<BookResponse> searchBooks(String title, String author, Integer availability, Genre genre, Float minPrice, Float maxPrice, Pageable pageable) {
    return bookRepository.searchBooks(title, author, genre, availability, minPrice, maxPrice, pageable)
            .map(BookMapper::toResponse);
}
}
