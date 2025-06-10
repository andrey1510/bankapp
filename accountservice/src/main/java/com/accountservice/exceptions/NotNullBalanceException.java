package com.accountservice.exceptions;

public class NotNullBalanceException extends RuntimeException {
    public NotNullBalanceException(String message) {
        super(message);
    }
}
