package com.hoseo.hackathon.storeticketingservice.exception;

public class DuplicateTicketingException extends RuntimeException{
    public DuplicateTicketingException(String msg) {
        super(msg);
    }
}
