package com.frontservice.exceptions;

public class PasswordsAreNotEqualException extends RuntimeException {
    public PasswordsAreNotEqualException(String message) {
        super(message);
    }
}
