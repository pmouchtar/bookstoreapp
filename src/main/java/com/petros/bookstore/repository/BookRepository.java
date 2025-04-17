package com.petros.bookstore.repository;

import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.enums.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE " +
            "(:title IS NULL OR b.title LIKE %:title%) AND " +
            "(:author IS NULL OR b.author LIKE %:author%) AND " +
            "(:genre IS NULL OR b.genre LIKE :genre) AND " +
            "(:availability IS NULL OR b.availability >= :availability) AND " +
            "(:minPrice IS NULL OR b.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR b.price <= :maxPrice)")
    Page<Book> searchBooks(
            @Param("title") String title,
            @Param("author") String author,
            @Param("genre") Genre genre,
            @Param("availability") Integer availability,
            @Param("minPrice") Float minPrice,
            @Param("maxPrice") Float maxPrice,
            Pageable pageable);
}
