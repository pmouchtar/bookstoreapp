package com.petros.bookstore.config;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestPropertySource(properties = {"spring.datasource.driver-class-name=org.postgresql.Driver"})
public abstract class AbstractPostgresContainerTest {

  @Container
  static PostgreSQLContainer<?> postgresContainer =
      new PostgreSQLContainer<>("postgres:16")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @BeforeAll
  static void init() {
    postgresContainer.start();

    System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
    System.setProperty("spring.datasource.username", postgresContainer.getUsername());
    System.setProperty("spring.datasource.password", postgresContainer.getPassword());
  }
}
