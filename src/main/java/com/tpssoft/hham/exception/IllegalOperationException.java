package com.tpssoft.hham.exception;

public class IllegalOperationException extends RuntimeException {
    public IllegalOperationException() {
        super();
    }

    public IllegalOperationException(String message) {
        super(message);
    }
}
