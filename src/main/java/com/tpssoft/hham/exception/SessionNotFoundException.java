package com.tpssoft.hham.exception;

public class SessionNotFoundException extends ResourceNotFoundException {
    public SessionNotFoundException() {
        super("Session not found");
    }

    public SessionNotFoundException(String message) {
        super(message);
    }
}
