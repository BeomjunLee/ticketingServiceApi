package com.hoseo.hackathon.storeticketingservice.global.security.exception;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
