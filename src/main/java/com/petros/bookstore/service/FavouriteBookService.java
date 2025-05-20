package com.petros.bookstore.service;

import com.petros.bookstore.dto.FavouriteBookRequest;
import com.petros.bookstore.dto.FavouriteBookResponse;
import com.petros.bookstore.exception.ResourceAlreadyExistsException;
import com.petros.bookstore.exception.ResourceNotFoundException;
import com.petros.bookstore.mapper.FavouriteBookMapper;
import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.Favourite_Book;
import com.petros.bookstore.model.User;
import com.petros.bookstore.repository.BookRepository;
import com.petros.bookstore.repository.FavouriteBookRepository;
import com.petros.bookstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavouriteBookService {

    private final FavouriteBookRepository favouriteRepo;
    private final UserRepository userRepo;
    private final BookRepository bookRepository;

    @Transactional
    public FavouriteBookResponse addToFavourites(Long userId, FavouriteBookRequest request) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        favouriteRepo.findByUserAndBook_Id(user, book.getId())
                .ifPresent(f -> {
                    throw new ResourceAlreadyExistsException("Book already in favourites");
                });

        Favourite_Book favourite = new Favourite_Book();
        favourite.setUser(user);
        favourite.setBook(book);

        Favourite_Book saved = favouriteRepo.save(favourite);
        return FavouriteBookMapper.toDto(saved);
    }

    @Transactional()
    public Page<FavouriteBookResponse> getFavourites(Long userId, Pageable pageable) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return favouriteRepo.findByUser(user, pageable)
                .map(FavouriteBookMapper::toDto);
    }

    @Transactional
    public void removeFromFavourites(Long userId, Long bookId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean exists = favouriteRepo.findByUserAndBook_Id(user, bookId).isPresent();
        if (!exists) throw new ResourceNotFoundException("Favourite not found");

        favouriteRepo.deleteByUserAndBook_Id(user, bookId);
    }
}