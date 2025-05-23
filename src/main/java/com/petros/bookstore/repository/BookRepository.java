package com.petros.bookstore.repository;

import com.petros.bookstore.model.Book;
import com.petros.bookstore.model.enums.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Book} entities. Provides basic CRUD operations and a
 * custom search method with filtering.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

  /**
   * Searches for books with optional filtering by title, author, genre, availability, and price
   * range. All filters are applied only if their corresponding parameters are not null.
   *
   * @param title Partial or full book title (case-insensitive).
   * @param author Partial or full author name (case-insensitive).
   * @param genre Genre of the book (exact match).
   * @param availability Minimum number of copies available.
   * @param minPrice Minimum price.
   * @param maxPrice Maximum price.
   * @param pageable Pageable object for pagination and sorting.
   * @return A page of books matching the specified criteria.
   */
  @Query(
      "SELECT b FROM Book b WHERE "
          + "(:title IS NULL OR b.title ILIKE %:title%) AND "
          + "(:author IS NULL OR b.author ILIKE %:author%) AND "
          + "(:genre IS NULL OR b.genre ILIKE :genre) AND "
          + "(:availability IS NULL OR b.availability >= :availability) AND "
          + "(:minPrice IS NULL OR b.price >= :minPrice) AND "
          + "(:maxPrice IS NULL OR b.price <= :maxPrice)")
  Page<Book> searchBooks(
      @Param("title") String title,
      @Param("author") String author,
      @Param("genre") Genre genre,
      @Param("availability") Integer availability,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice,
      Pageable pageable);
}
