package com.hoseo.hackathon.storeticketingservice.exception;

public class DuplicateStoreNameException extends RuntimeException{
    public DuplicateStoreNameException(String message) {
        super(message);
    }
}
