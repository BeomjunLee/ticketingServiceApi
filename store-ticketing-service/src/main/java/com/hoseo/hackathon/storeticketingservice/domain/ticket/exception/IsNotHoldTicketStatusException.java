package com.hoseo.hackathon.storeticketingservice.domain.ticket.exception;

public class IsNotHoldTicketStatusException extends RuntimeException{
    public IsNotHoldTicketStatusException(String message) {
        super(message);
    }
}
