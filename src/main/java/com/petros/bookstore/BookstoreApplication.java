package com.petros.bookstore;

import com.petros.bookstore.model.Book;
import com.petros.bookstore.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);
	}
	@Bean
	CommandLineRunner init(BookRepository repo) {
		return args -> {
			repo.save(new Book("Clean Code", "Uncle Bob"));
			repo.save(new Book("Domain-Driven Design", "Eric Evans"));
			System.out.println("Books inserted: " + repo.findAll().size());
		};
	}
}
