package com.hoseo.hackathon.storeticketingservice.domain.member.exception;

public class RefreshTokenGrantTypeException extends RuntimeException {
    public RefreshTokenGrantTypeException(String message) {
        super(message);
    }
}
