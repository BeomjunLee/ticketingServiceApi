package com.hoseo.hackathon.storeticketingservice.exception;

public class NotMatchedRefreshTokenException extends RuntimeException {
    public NotMatchedRefreshTokenException(String message) {
        super(message);
    }
}
