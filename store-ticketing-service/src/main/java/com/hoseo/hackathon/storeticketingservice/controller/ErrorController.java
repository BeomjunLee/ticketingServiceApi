package com.hoseo.hackathon.storeticketingservice.controller;

import com.hoseo.hackathon.storeticketingservice.domain.response.Response;
import com.hoseo.hackathon.storeticketingservice.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class ErrorController {

    /**
     * Valid 에러
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity validate(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//        StringBuilder builder = new StringBuilder();
//        for (FieldError fieldError : bindingResult.getFieldErrors()) {
//            builder.append("[error필드: ");
//            builder.append(fieldError.getField());
//            builder.append(", error메세지: ");
//            builder.append(fieldError.getDefaultMessage());
//            builder.append(", 입력 값: ");
//            builder.append(fieldError.getRejectedValue());
//            builder.append("] ");
//        }
        Response response = Response.builder()
                .result("fail")
                .status(400)
//                .message(builder.toString())
                .message(fieldErrors.get(0).getDefaultMessage())    //첫번째 에러만
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 티켓 중복 Error
     */
    @ExceptionHandler(DuplicateTicketingException.class)
    public ResponseEntity ticketDuplicated(DuplicateTicketingException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result("fail")
                .status(409)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    /**
     * 번호표 찾기 실패
     */
    @ExceptionHandler(NotFoundTicketException.class)
    public ResponseEntity notFoundTicket(NotFoundTicketException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result("fail")
                .status(404)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    /**
     * 가게 찾기 실패
     */
    @ExceptionHandler(NotFoundStoreException.class)
    public ResponseEntity notFoundStore(NotFoundStoreException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result("fail")
                .status(404)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    /**
     * 유저 찾기 실패
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity notFoundStore(UsernameNotFoundException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result("fail")
                .status(404)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * id값으로 찾기 실패
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity notFoundById(NoSuchElementException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result("fail")
                .status(404)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 가게 번호표가 비활성화인데 번호표 발급시 에러
     */
    @ExceptionHandler(StoreTicketIsCloseException.class)
    public ResponseEntity notFoundStore(StoreTicketIsCloseException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result("fail")
                .status(403)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * 아이디 중복 에러
     */
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity duplicatedUsername(DuplicateUsernameException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result("fail")
                .status(400)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 가게명 중복 에러
     */
    @ExceptionHandler(DuplicateStoreNameException.class)
    public ResponseEntity duplicatedStoreName(DuplicateStoreNameException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result("fail")
                .status(400)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 이미 처리됨
     */
    @ExceptionHandler(IsAlreadyCompleteException.class)
    public ResponseEntity isAlreadyComplete(IsAlreadyCompleteException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result("fail")
                .status(208)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(response);
    }

    /**
     *  승인되지 않은 가게
     */
    @ExceptionHandler(NotAuthorizedStoreException.class)
    public ResponseEntity notAuthorizedStore(NotAuthorizedStoreException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result("fail")
                .status(401)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
