package com.petros.bookstore.repository;

import com.petros.bookstore.model.Favourite_Book;
import com.petros.bookstore.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavouriteBookRepository extends JpaRepository<Favourite_Book, Long> {

  Page<Favourite_Book> findByUser(User user, Pageable pageable);

  Optional<Favourite_Book> findByUserAndBook_Id(User user, Long bookId);

  void deleteByUserAndBook_Id(User user, Long bookId);
}
