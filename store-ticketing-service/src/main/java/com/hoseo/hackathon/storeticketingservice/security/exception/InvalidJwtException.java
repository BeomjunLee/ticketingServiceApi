package com.hoseo.hackathon.storeticketingservice.security.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidJwtException extends RuntimeException{

    public InvalidJwtException(String msg) {
        super(msg);
    }
}


