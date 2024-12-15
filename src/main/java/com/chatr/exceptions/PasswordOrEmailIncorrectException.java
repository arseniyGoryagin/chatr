package com.chatr.exceptions;

public class PasswordOrEmailIncorrectException extends RuntimeException {
    public PasswordOrEmailIncorrectException(String message) {
        super(message);
    }
}
