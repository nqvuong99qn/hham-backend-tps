package com.tpssoft.hham.exception;

public class TransactionNotFoundException extends ResourceNotFoundException {
    public TransactionNotFoundException() {
        super("Transaction not found");
    }

    public TransactionNotFoundException(String message) {
        super(message);
    }
}
