package com.example.scrapeservice.exceptions;

public class ProductException extends RuntimeException {

    public ProductException(String message) {
        super(message);
    }

    public ProductException(String message, Exception e) {
        super(message);
    }
}
