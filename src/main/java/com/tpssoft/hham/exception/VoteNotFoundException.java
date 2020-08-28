package com.tpssoft.hham.exception;

public class VoteNotFoundException extends ResourceNotFoundException {
    public VoteNotFoundException() {
        super("Vote not found");
    }

    public VoteNotFoundException(String message) {
        super(message);
    }
}
