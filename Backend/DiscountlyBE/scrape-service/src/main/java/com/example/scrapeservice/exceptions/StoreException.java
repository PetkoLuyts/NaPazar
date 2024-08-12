package com.example.scrapeservice.exceptions;

public class StoreException extends RuntimeException {
    public StoreException(String message) {
        super(message);
    }

    public StoreException(String message, Exception e) {
        super(message);
    }
}
