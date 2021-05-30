package com.hoseo.hackathon.storeticketingservice.domain.store.exception;

public class NotAuthorizedStoreException extends RuntimeException {
    public NotAuthorizedStoreException(String message) {
        super(message);
    }
}
