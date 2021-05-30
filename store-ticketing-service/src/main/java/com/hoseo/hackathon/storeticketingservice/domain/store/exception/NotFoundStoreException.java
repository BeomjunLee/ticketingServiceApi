package com.hoseo.hackathon.storeticketingservice.domain.store.exception;

public class NotFoundStoreException extends RuntimeException{
    public NotFoundStoreException(String message) {
        super(message);
    }
}
