package com.tpssoft.hham.exception;

public class InvitationNotFoundException extends ResourceNotFoundException {
    public InvitationNotFoundException() {
        super("Invitation not found");
    }

    public InvitationNotFoundException(String message) {
        super(message);
    }
}
