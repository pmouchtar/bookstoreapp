package com.petros.bookstore.service;

import com.petros.bookstore.dto.BookRequest;
import com.petros.bookstore.dto.BookResponse;
import com.petros.bookstore.mapper.BookMapper;
import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.enums.Genre;
import com.petros.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream()
                .map(BookMapper::toResponse)
                .toList();
    }

    public Optional<BookResponse> findBookById(Long id) {
        return bookRepository.findById(id).map(BookMapper::toResponse);
    }

    public Optional<BookResponse> updateBook(Long id, BookRequest request) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(request.getTitle());
            book.setAuthor(request.getAuthor());
            book.setDescription(request.getDescription());
            book.setPrice(request.getPrice());
            book.setAvailability(request.getAvailability());
            book.setGenre(Genre.valueOf(request.getGenre().toString()));
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

    public List<BookResponse> searchBooks(String title, String author, String genre, Float minPrice, Float maxPrice) {
        if ((minPrice == null && maxPrice != null) || minPrice != null && maxPrice == null) {
            // catch exception of invalid arguments
        }
        return bookRepository.findAll().stream()
                .filter(book -> (title == null || book.getTitle().toLowerCase().contains(title.toLowerCase())) &&
                        (author == null || book.getAuthor().toLowerCase().contains(author.toLowerCase())) &&
                        (genre == null || book.getGenre().name().equalsIgnoreCase(genre)) &&
                        (minPrice == null || book.getPrice() >= minPrice) &&
                        (maxPrice == null || book.getPrice() <= maxPrice))
                .map(BookMapper::toResponse)
                .toList();
    }
}
