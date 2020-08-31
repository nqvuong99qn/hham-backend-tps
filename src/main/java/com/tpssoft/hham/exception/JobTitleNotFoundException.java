package com.tpssoft.hham.exception;

public class JobTitleNotFoundException extends ResourceNotFoundException {
    public JobTitleNotFoundException() {
        super("Job title not found");
    }
    public JobTitleNotFoundException(String message) {
        super(message);
    }
}
