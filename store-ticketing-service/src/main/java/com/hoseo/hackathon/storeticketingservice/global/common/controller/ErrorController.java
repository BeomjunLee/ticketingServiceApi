package com.hoseo.hackathon.storeticketingservice.global.common.controller;

import com.hoseo.hackathon.storeticketingservice.global.common.dto.Response;
import com.hoseo.hackathon.storeticketingservice.global.common.dto.ResultStatus;
import com.hoseo.hackathon.storeticketingservice.global.common.exception.IsAlreadyCompleteException;
import com.hoseo.hackathon.storeticketingservice.domain.member.exception.*;
import com.hoseo.hackathon.storeticketingservice.domain.store.exception.NotAuthorizedStoreException;
import com.hoseo.hackathon.storeticketingservice.domain.store.exception.NotFoundStoreException;
import com.hoseo.hackathon.storeticketingservice.domain.store.exception.StoreTicketIsCloseException;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.exception.DuplicateTicketingException;
import com.hoseo.hackathon.storeticketingservice.global.common.exception.NotFoundTicketException;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.exception.IsNotHoldTicketStatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;

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
        Response response = Response.builder()
                .result(ResultStatus.FAIL)
                .status(BAD_REQUEST.value())
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
                .result(ResultStatus.FAIL)
                .status(CONFLICT.value())
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
                .result(ResultStatus.FAIL)
                .status(BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }
    /**
     * 매장 찾기 실패
     */
    @ExceptionHandler(NotFoundStoreException.class)
    public ResponseEntity notFoundStore(NotFoundStoreException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result(ResultStatus.FAIL)
                .status(BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }
    /**
     * 유저 찾기 실패
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity notFoundStore(UsernameNotFoundException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result(ResultStatus.FAIL)
                .status(BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

    /**
     * id 값으로 찾기 실패
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity notFoundById(NoSuchElementException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result(ResultStatus.FAIL)
                .status(BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

    /**
     * 매장 번호표가 비활성화인데 번호표 발급시 에러
     */
    @ExceptionHandler(StoreTicketIsCloseException.class)
    public ResponseEntity notFoundStore(StoreTicketIsCloseException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result(ResultStatus.FAIL)
                .status(CONFLICT.value())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 매장 보류 번호표 체크, 취소시 HOLD 상태가 아닌 경우
     */
    @ExceptionHandler(IsNotHoldTicketStatusException.class)
    public ResponseEntity isNotHoldTicket(IsNotHoldTicketStatusException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result(ResultStatus.FAIL)
                .status(CONFLICT.value())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 아이디 중복 에러
     */
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity duplicatedUsername(DuplicateUsernameException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result(ResultStatus.FAIL)
                .status(BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

    /**
     * 매장명 중복 에러
     */
    @ExceptionHandler(DuplicateStoreNameException.class)
    public ResponseEntity duplicatedStoreName(DuplicateStoreNameException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result(ResultStatus.FAIL)
                .status(BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

    /**
     * 작업들이 이미 처리돼서 충돌
     */
    @ExceptionHandler(IsAlreadyCompleteException.class)
    public ResponseEntity isAlreadyComplete(IsAlreadyCompleteException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result(ResultStatus.FAIL)
                .status(CONFLICT.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     *  승인되지 않은 가게
     */
    @ExceptionHandler(NotAuthorizedStoreException.class)
    public ResponseEntity notAuthorizedStore(NotAuthorizedStoreException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result(ResultStatus.FAIL)
                .status(FORBIDDEN.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * RefreshToken 못찾음
     */
    @ExceptionHandler(NotFoundRefreshTokenException.class)
    public ResponseEntity notFoundRefreshToken(NotFoundRefreshTokenException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result(ResultStatus.FAIL)
                .status(BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

    /**
     * RefreshToken(Client), RefreshToken(DB) 비교 오류
     */
    @ExceptionHandler(NotMatchedRefreshTokenException.class)
    public ResponseEntity notMatchedRefreshToken(NotMatchedRefreshTokenException e) {
        log.error(e.getMessage());
        Response response = Response.builder()
                .result(ResultStatus.FAIL)
                .status(BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

    /**
     * 아이디, 비번 로그인 검증 오류
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity badCredentials(Exception e) {
        Response response = Response.builder()
                .result(ResultStatus.FAIL)
                .status(BAD_REQUEST.value())
                .message("로그인 실패")
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 올바르지않은 refreshToken
     */
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity invalidRefreshToken(Exception e) {
        Response response = Response.builder()
                .result(ResultStatus.FAIL)
                .status(BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }
}
