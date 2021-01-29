package com.hoseo.hackathon.storeticketingservice.exception;

public class NotOpenStoreException extends RuntimeException {
    public NotOpenStoreException(String message) {
        super(message);
    }
}
