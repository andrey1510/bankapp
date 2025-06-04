package com.accountservice.exceptions;

public class AccountIsDisabledException extends RuntimeException {
    public AccountIsDisabledException(String message) {
        super(message);
    }
}
