package com.hoseo.hackathon.storeticketingservice.exception;

public class NewAccessTokenIssuedException extends RuntimeException {
    public NewAccessTokenIssuedException(String message) {
        super(message);
    }
}
