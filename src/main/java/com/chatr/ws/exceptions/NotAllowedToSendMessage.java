package com.chatr.ws.exceptions;

public class NotAllowedToSendMessage extends RuntimeException {
    public NotAllowedToSendMessage(String message) {
        super(message);
    }
}
