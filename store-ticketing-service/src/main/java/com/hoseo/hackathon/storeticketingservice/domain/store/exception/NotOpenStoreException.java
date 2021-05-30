package com.hoseo.hackathon.storeticketingservice.domain.store.exception;

public class NotOpenStoreException extends RuntimeException {
    public NotOpenStoreException(String message) {
        super(message);
    }
}
