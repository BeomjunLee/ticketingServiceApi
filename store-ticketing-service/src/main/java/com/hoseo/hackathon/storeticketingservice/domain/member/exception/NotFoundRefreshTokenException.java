package com.hoseo.hackathon.storeticketingservice.domain.member.exception;

public class NotFoundRefreshTokenException extends RuntimeException {
    public NotFoundRefreshTokenException(String message) {
        super(message);
    }
}
