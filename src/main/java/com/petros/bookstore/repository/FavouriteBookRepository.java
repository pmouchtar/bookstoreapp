package com.petros.bookstore.repository;

import com.petros.bookstore.model.FavouriteBook;
import com.petros.bookstore.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavouriteBookRepository extends JpaRepository<FavouriteBook, Long> {

    Page<FavouriteBook> findByUser(User user, Pageable pageable);

    Optional<FavouriteBook> findByUserAndBook_Id(User user, Long bookId);

    void deleteByUserAndBook_Id(User user, Long bookId);
}
