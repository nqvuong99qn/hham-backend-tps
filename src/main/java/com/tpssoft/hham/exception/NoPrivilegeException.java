package com.tpssoft.hham.exception;

public class NoPrivilegeException extends RuntimeException {
    public NoPrivilegeException() {
        super("Insufficient privilege");
    }

    public NoPrivilegeException(String message) {
        super(message);
    }
}
