package com.hoseo.hackathon.storeticketingservice.exception;

public class NotFoundTicketException extends RuntimeException{
    public NotFoundTicketException(String message) {
        super(message);
    }
}
