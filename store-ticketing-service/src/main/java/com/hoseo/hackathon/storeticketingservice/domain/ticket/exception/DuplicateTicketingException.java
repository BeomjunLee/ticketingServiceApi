package com.hoseo.hackathon.storeticketingservice.domain.ticket.exception;

public class DuplicateTicketingException extends RuntimeException{
    public DuplicateTicketingException(String msg) {
        super(msg);
    }
}
