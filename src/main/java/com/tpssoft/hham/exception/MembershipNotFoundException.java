package com.tpssoft.hham.exception;

public class MembershipNotFoundException extends ResourceNotFoundException {
    public MembershipNotFoundException() {
        super("Membership not found");
    }

    public MembershipNotFoundException(String message) {
        super(message);
    }
}
