package com.hoseo.hackathon.storeticketingservice.domain.member.exception;

public class DuplicateUsernameException extends RuntimeException{
    public DuplicateUsernameException(String message) {
        super(message);
    }
}
