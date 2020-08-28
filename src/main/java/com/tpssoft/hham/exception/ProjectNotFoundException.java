package com.tpssoft.hham.exception;

public class ProjectNotFoundException extends ResourceNotFoundException {
    public ProjectNotFoundException() {
        super("Project not found");
    }

    public ProjectNotFoundException(String message) {
        super(message);
    }
}
