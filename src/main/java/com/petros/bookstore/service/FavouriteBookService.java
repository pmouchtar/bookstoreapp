package com.petros.bookstore.service;

import com.petros.bookstore.dto.FavouriteBookDTO.FavouriteBookRequestDto;
import com.petros.bookstore.dto.FavouriteBookDTO.FavouriteBookResponseDto;
import com.petros.bookstore.exception.customException.ResourceAlreadyExistsException;
import com.petros.bookstore.exception.customException.ResourceNotFoundException;
import com.petros.bookstore.mapper.FavouriteBookMapper;
import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.FavouriteBook;
import com.petros.bookstore.model.User;
import com.petros.bookstore.repository.BookRepository;
import com.petros.bookstore.repository.FavouriteBookRepository;
import com.petros.bookstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing users' favourite books.
 * Provides functionality for adding, retrieving, and removing favourite books
 * for a given user.
 */
@Service
@RequiredArgsConstructor
public class FavouriteBookService {

    private final FavouriteBookRepository favouriteRepo;
    private final UserRepository userRepo;
    private final BookRepository bookRepository;

    /**
     * Adds a book to a user's list of favourites.
     *
     * @param userId
     *            The ID of the user adding the favourite.
     * @param request
     *            The favourite book request containing the book ID.
     * @return A {@link FavouriteBookResponseDto} representing the saved favourite.
     * @throws ResourceNotFoundException
     *             If the user or book is not found.
     * @throws ResourceAlreadyExistsException
     *             If the book is already marked as favourite by the user.
     */
    @Transactional
    public FavouriteBookResponseDto addToFavourites(Long userId, FavouriteBookRequestDto request) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(//
                "User not found"));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        favouriteRepo.findByUserAndBook_Id(user, book.getId()).ifPresent(f -> {
            throw new ResourceAlreadyExistsException("Book already in favourites");
        });

        FavouriteBook favourite = new FavouriteBook();
        favourite.setUser(user);
        favourite.setBook(book);

        FavouriteBook saved = favouriteRepo.save(favourite);
        return FavouriteBookMapper.toDto(saved);
    }

    /**
     * Retrieves a paginated list of a user's favourite books.
     *
     * @param userId
     *            The ID of the user whose favourites are being requested.
     * @param pageable
     *            Pagination parameters.
     * @return A paginated list of {@link FavouriteBookResponseDto}.
     * @throws ResourceNotFoundException
     *             If the user is not found.
     */
    @Transactional
    public Page<FavouriteBookResponseDto> getFavourites(Long userId, Pageable pageable) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(//
                "User not found"));

        return favouriteRepo.findByUser(user, pageable).map(FavouriteBookMapper::toDto);
    }

    /**
     * Removes a specific book from a user's favourites.
     *
     * @param userId
     *            The ID of the user.
     * @param bookId
     *            The ID of the book to be removed.
     * @throws ResourceNotFoundException
     *             If the user is not found or the favourite does not exist.
     */
    @Transactional
    public void removeFromFavourites(Long userId, Long bookId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(//
                "User not found"));

        boolean exists = favouriteRepo.findByUserAndBook_Id(user, bookId).isPresent();
        if (!exists) {
            throw new ResourceNotFoundException("Favourite not found");
        }

        favouriteRepo.deleteByUserAndBook_Id(user, bookId);
    }
}
