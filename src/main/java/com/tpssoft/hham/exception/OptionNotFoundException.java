package com.tpssoft.hham.exception;

public class OptionNotFoundException extends ResourceNotFoundException {
    public OptionNotFoundException() {
        super("Option not found");
    }

    public OptionNotFoundException(String message) {
        super(message);
    }
}
