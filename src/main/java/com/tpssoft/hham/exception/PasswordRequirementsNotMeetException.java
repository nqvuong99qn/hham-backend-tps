package com.tpssoft.hham.exception;

public class PasswordRequirementsNotMeetException extends IllegalArgumentException {
    public PasswordRequirementsNotMeetException() {
        super();
    }

    public PasswordRequirementsNotMeetException(String message) {
        super(message);
    }
}
