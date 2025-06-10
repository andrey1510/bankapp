package com.accountservice.exceptions;

public class WrongAgeException extends RuntimeException {
    public WrongAgeException(String message) {
        super(message);
    }
}
