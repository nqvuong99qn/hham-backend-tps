package com.tpssoft.hham.exception;

public class DuplicatedEmailException extends IllegalArgumentException {
    public DuplicatedEmailException() {
        super("Duplicated email");
    }

    public DuplicatedEmailException(String message) {
        super(message);
    }
}
