package com.hoseo.hackathon.storeticketingservice.exception;

public class NotFoundStoreException extends RuntimeException{
    public NotFoundStoreException(String message) {
        super(message);
    }
}
