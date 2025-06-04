package com.cashservice.exceptions;

public class CashOperationException extends RuntimeException {
    public CashOperationException(String message) {
        super(message);
    }
}
