package com.petros.bookstore.exception;

public class ResourceGoneException extends RuntimeException {
    public ResourceGoneException(String message) {
        super(message);
    }
}
