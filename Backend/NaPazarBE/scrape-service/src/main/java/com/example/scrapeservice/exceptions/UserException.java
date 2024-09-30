package com.example.scrapeservice.exceptions;

public class UserException extends RuntimeException {

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Exception e) {
        super(message);
    }
}
