package com.petros.bookstore.controller;

import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.Favourite_Book;
import com.petros.bookstore.model.User;
import com.petros.bookstore.repository.BookRepository;
import com.petros.bookstore.repository.FavouriteBookRepository;
import com.petros.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
public class TestController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FavouriteBookRepository favouriteBookRepository;
    @Autowired
    private BookRepository bookRepository;

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/favourite-books")
    public Favourite_Book addBookToFavourites(@RequestParam Long userId, @RequestParam Long bookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));

        Favourite_Book favouriteBook = new Favourite_Book();
        favouriteBook.setUser(user);
        favouriteBook.setBook(book);

        return favouriteBookRepository.save(favouriteBook);
    }

    @DeleteMapping("/favourite-books/{id}")
    public ResponseEntity<Void> deleteFavourite(@PathVariable Long id) {
        if (favouriteBookRepository.existsById(id)) {
            favouriteBookRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    @PostMapping("/books")
//    public Book addBook(@RequestBody Book book) {
//        return bookRepository.save(book);
//    }
//
//    @GetMapping("/books")
//    public List<Book> getAllBooks() {
//        return bookRepository.findAll();
//    }
}
