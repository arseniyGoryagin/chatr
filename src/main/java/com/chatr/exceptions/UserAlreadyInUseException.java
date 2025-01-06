package com.chatr.exceptions;

public class UserAlreadyInUseException extends RuntimeException {
    public UserAlreadyInUseException(String message) {
        super(message);
    }
}
