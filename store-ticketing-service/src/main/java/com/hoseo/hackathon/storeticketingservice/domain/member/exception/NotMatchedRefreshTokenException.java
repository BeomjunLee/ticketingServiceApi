package com.hoseo.hackathon.storeticketingservice.domain.member.exception;

public class NotMatchedRefreshTokenException extends RuntimeException {
    public NotMatchedRefreshTokenException(String message) {
        super(message);
    }
}
