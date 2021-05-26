package com.hoseo.hackathon.storeticketingservice.exception;

public class IsNotHoldTicketStatusException extends RuntimeException{
    public IsNotHoldTicketStatusException(String message) {
        super(message);
    }
}
