package com.petros.bookstore.repository;

import com.petros.bookstore.model.Favourite_Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavouriteBookRepository extends JpaRepository<Favourite_Book, Long> {
}
