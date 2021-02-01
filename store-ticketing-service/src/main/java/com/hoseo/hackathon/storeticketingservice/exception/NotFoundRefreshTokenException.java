package com.hoseo.hackathon.storeticketingservice.exception;

public class NotFoundRefreshTokenException extends RuntimeException {
    public NotFoundRefreshTokenException(String message) {
        super(message);
    }
}
