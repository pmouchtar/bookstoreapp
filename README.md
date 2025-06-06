## Bookstore API

A Spring Boot RESTful API for managing books, users, shopping carts, orders, and favorites.

## Technologies

- Java 17+
- Spring Boot
- Spring Data JPA
- JWT Authentication
- OpenSSL and RSA256 encryption
- PostgreSQL
- Flyway (Database Migration)
- Maven
- Docker
- Testcontainers (Integration Testing)
- JUnit 5
- Swagger UI (API Documentation)
- Lombok

## Features
- dto's
- mappers
- controller/service/repository architecture
- pagination
- filtering
- jwt authentication and spring security
- role-based access
- exception handling
- javadoc
- checkstyle
- spotless formatter

## 1. Clone the Repository

- bash
- git clone https://github.com/pmouchtar/bookstore.git
- cd bookstore

## 2. Setup PostgreSQL

- Create a database named bookstore
- Configure application.properties:

- spring.datasource.url=jdbc:postgresql://localhost:5432/bookstore
- spring.datasource.username=your_username
- spring.datasource.password=your_password

## 3. Build and Run the Application

- mvn clean install
- mvn spring-boot:run

## 4. Once the app is running, access Swagger UI:

http://localhost:8080/swagger-ui.html

or for Postman access:

http://localhost:8080

## 5. For testing use the dev branch
