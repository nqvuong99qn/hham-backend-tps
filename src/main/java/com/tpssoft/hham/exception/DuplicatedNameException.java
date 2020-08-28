package com.tpssoft.hham.exception;

public class DuplicatedNameException extends IllegalArgumentException{
    public DuplicatedNameException() {
        super("Duplicated name");
    }

    public DuplicatedNameException(String message) {
        super(message);
    }
}
