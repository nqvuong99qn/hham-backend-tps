package com.tpssoft.hham.exception;

public class AuthenticationFailureException extends RuntimeException {
    public AuthenticationFailureException() {
        super("Authentication failure");
    }

    public AuthenticationFailureException(String message) {
        super(message);
    }
}
