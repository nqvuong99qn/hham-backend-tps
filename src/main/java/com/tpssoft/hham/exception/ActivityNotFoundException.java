package com.tpssoft.hham.exception;

public class ActivityNotFoundException extends ResourceNotFoundException {
    public ActivityNotFoundException() {
        super("Activity not found");
    }

    public ActivityNotFoundException(String message) {
        super(message);
    }
}
