package com.transferservice.exceptions;

public class TransferOperationException extends RuntimeException {
    public TransferOperationException(String message) {
        super(message);
    }
}
