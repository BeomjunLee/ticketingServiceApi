package com.hoseo.hackathon.storeticketingservice.exception;

public class NotAuthorizedStoreException extends RuntimeException {
    public NotAuthorizedStoreException(String message) {
        super(message);
    }
}
