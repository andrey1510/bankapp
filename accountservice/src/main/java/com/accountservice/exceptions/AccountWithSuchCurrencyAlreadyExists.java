package com.accountservice.exceptions;

public class AccountWithSuchCurrencyAlreadyExists extends RuntimeException {
    public AccountWithSuchCurrencyAlreadyExists(String message) {
        super(message);
    }
}
