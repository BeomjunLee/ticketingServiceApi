package com.hoseo.hackathon.storeticketingservice.domain.ticket.controller;

import com.hoseo.hackathon.storeticketingservice.global.common.dto.Response;
import com.hoseo.hackathon.storeticketingservice.global.common.dto.ResultStatus;
import com.hoseo.hackathon.storeticketingservice.domain.store.controller.StoreController;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.dto.form.TicketForm;
import com.hoseo.hackathon.storeticketingservice.domain.ticket.service.TicketService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * [회원] 번호표 뽑기
     * @param store_id 매장 고유 id 값
     * @param ticketForm 번호표 발급 form
     * @param principal jwt 요청 회원 정보
     * @return http response
     */
    @ApiOperation(value = "번호표 뽑기[회원]", notes = "매장의 번호표를 발급 받습니다")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/stores/{store_id}/tickets/new")
    public ResponseEntity createTicket(@PathVariable("store_id") Long store_id, @Valid @RequestBody TicketForm ticketForm, Principal principal) {

        ticketService.createTicket(ticketForm, store_id, principal.getName());

        URI createdUri = linkTo(StoreController.class).slash(store_id).slash("tickets/new").toUri();

        return ResponseEntity.created(createdUri).body(Response.builder()
                .result(ResultStatus.SUCCESS)
                .status(200)
                .message("번호표 발급 성공")
                .build());
    }

    /**
     * 번호표 체크
     * @param ticket_id 번호표 고유 id 값
     * @param principal access_token 에서 추출한 회원 정보
     * @return http response
     */
    @ApiOperation(value = "현재 대기중인 번호표 체크하기[매장 관리자]", notes = "현재 대기 회원의 번호표를 체크합니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PostMapping("/tickets/{ticket_id}/check-ticket")
    public ResponseEntity checkTicket(@PathVariable("ticket_id")Long ticket_id, Principal principal) {
        ticketService.checkTicket(principal.getName(), ticket_id);

        return ResponseEntity.ok(Response.builder()
                .result(ResultStatus.SUCCESS)
                .status(200)
                .message("번호표 체크 성공")
                .build());
    }

    /**
     * 번호표 취소
     * @param ticket_id 번호표 고유 id 값
     * @param principal access_token 에서 추출한 회원 정보
     * @return http response
     */
    @ApiOperation(value = "현재 대기중인 번호표 취소[매장 관리자]", notes = "현재 대기 회원의 번호표를 취소합니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PostMapping("/tickets/{ticket_id}/cancel-ticket")
    public ResponseEntity cancelTicket(@PathVariable("ticket_id")Long ticket_id, Principal principal) {
        ticketService.cancelTicket(principal.getName(), ticket_id);

        return ResponseEntity.ok(Response.builder()
                .result(ResultStatus.SUCCESS)
                .status(200)
                .message("번호표 취소 성공")
                .build());
    }

    /**
     * 번호표 보류
     * @param ticket_id 번호표 고유 id 값
     * @param principal access_token 에서 추출한 회원 정보
     * @return http response
     */
    @ApiOperation(value = "현재 대기중인 번호표 보류[매장 관리자]", notes = "현재 대기 회원의 번호표를 보류합니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PostMapping("/tickets/{ticket_id}/hold-ticket")
    public ResponseEntity holdTicket(@PathVariable("ticket_id")Long ticket_id, Principal principal) {
        ticketService.holdTicket(principal.getName(), ticket_id);

        return ResponseEntity.ok(Response.builder()
                .result(ResultStatus.SUCCESS)
                .status(200)
                .message("번호표 보류 성공")
                .build());
    }

    /**
     * 보류중인 번호표 체크
     * @param ticket_id 번호표 고유 id 값
     * @param principal access_token 에서 추출한 회원 정보
     * @return http response
     */
    @ApiOperation(value = "보류중인 번호표 체크[매장 관리자]", notes = "현재 보류중인 회원의 번호표를 체크합니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PostMapping("/tickets/{ticket_id}/check-holdingTicket")
    public ResponseEntity holdCheckTicket(@PathVariable("ticket_id")Long ticket_id, Principal principal) {
        ticketService.holdCheckTicket(principal.getName(), ticket_id);

        return ResponseEntity.ok(Response.builder()
                .result(ResultStatus.SUCCESS)
                .status(200)
                .message("보류 번호표 체크 성공")
                .build());
    }

    /**
     * 보류중인 번호표 취소
     * @param ticket_id 번호표 고유 id 값
     * @param principal access_token 에서 추출한 회원 정보
     * @return http response
     */
    @ApiOperation(value = "보류중인 번호표 취소[매장 관리자]", notes = "현재 보류중인 회원의 번호표를 취소합니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PostMapping("/tickets/{ticket_id}/cancel-holdingTicket")
    public ResponseEntity holdCancelTicket(@PathVariable("ticket_id")Long ticket_id, Principal principal) {
        ticketService.holdCancelTicket(principal.getName(), ticket_id);

        return ResponseEntity.ok(Response.builder()
                .result(ResultStatus.SUCCESS)
                .status(200)
                .message("보류 번호표 취소 성공")
                .build());
    }
}
