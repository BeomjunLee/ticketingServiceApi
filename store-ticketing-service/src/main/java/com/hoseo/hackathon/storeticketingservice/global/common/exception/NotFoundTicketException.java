package com.hoseo.hackathon.storeticketingservice.global.common.exception;

public class NotFoundTicketException extends RuntimeException{
    public NotFoundTicketException(String message) {
        super(message);
    }
}
