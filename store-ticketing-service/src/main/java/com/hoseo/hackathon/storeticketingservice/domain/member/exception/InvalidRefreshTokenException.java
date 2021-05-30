package com.hoseo.hackathon.storeticketingservice.domain.member.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
