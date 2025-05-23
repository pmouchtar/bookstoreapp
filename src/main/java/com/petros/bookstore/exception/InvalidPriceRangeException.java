package com.petros.bookstore.exception;

public class InvalidPriceRangeException extends RuntimeException {
  public InvalidPriceRangeException(String message) {
    super(message);
  }
}
