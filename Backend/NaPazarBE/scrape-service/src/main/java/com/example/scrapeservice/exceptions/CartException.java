package com.example.scrapeservice.exceptions;

public class CartException extends RuntimeException {
    public CartException(String message) {
        super(message);
    }

    public CartException(String message, Exception e) {
        super(message);
    }
}
