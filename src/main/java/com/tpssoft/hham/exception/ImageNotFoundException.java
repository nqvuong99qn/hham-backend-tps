package com.tpssoft.hham.exception;

public class ImageNotFoundException extends ResourceNotFoundException {
    public ImageNotFoundException() {
        super("Image not found");
    }

    public ImageNotFoundException(String message) {
        super(message);
    }
}
