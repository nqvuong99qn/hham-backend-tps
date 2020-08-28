package com.tpssoft.hham.exception;

public class NegativeFundException extends IllegalStateException {
    public NegativeFundException() {
        super();
    }

    public NegativeFundException(String message) {
        super(message);
    }
}
