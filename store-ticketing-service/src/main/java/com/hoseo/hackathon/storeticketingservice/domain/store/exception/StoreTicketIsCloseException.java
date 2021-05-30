package com.hoseo.hackathon.storeticketingservice.domain.store.exception;

public class StoreTicketIsCloseException extends RuntimeException{
    public StoreTicketIsCloseException(String message) {
        super(message);
    }
}
