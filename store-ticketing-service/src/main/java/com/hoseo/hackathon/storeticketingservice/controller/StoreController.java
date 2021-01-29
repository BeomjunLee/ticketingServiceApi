package com.hoseo.hackathon.storeticketingservice.controller;

import com.hoseo.hackathon.storeticketingservice.domain.Store;
import com.hoseo.hackathon.storeticketingservice.domain.Ticket;
import com.hoseo.hackathon.storeticketingservice.domain.dto.HoldingMembersDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.StoreManageDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.WaitingMembersDto;
import com.hoseo.hackathon.storeticketingservice.domain.form.AvgTimeForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.StoreNoticeForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.TicketForm;
import com.hoseo.hackathon.storeticketingservice.domain.resource.HoldingMembersResource;
import com.hoseo.hackathon.storeticketingservice.domain.resource.WaitingMembersAndStoreManageResource;
import com.hoseo.hackathon.storeticketingservice.domain.resource.WaitingMembersResource;
import com.hoseo.hackathon.storeticketingservice.domain.response.Response;
import com.hoseo.hackathon.storeticketingservice.service.StoreService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/stores")
public class StoreController {

    private final StoreService storeService;

//===========================================번호표 뽑기========================================
    /**
     * [회원]가게 번호표 뽑기
     * req : 인원수
     */
    @ApiOperation(value = "번호표 뽑기[회원]", notes = "가게의 번호표를 뽑습니다")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{store_id}/new/tickets")
    public ResponseEntity createTicket(@PathVariable("store_id") Long store_id, @Valid @RequestBody TicketForm ticketForm, Principal principal) {
        Ticket ticket = Ticket.builder()
                .peopleCount(ticketForm.getPeopleCount())
                .build();

        storeService.createTicket(ticket, store_id, principal.getName());

        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("번호표 발급 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    //===========================================가게 번호표 관리 메뉴========================================
    /**
     * [관리자] 대기중인 회원 리스트 관리 + 현재 가게 정보 관리
     * 응답 : 대기중인 회원정보, 가게 현재 상태, 총 대기인원, 총 대기시간, 공지사항, 한사람당 대기시간,
     * link : self, 대기 보류 취소 체크, 가게 번호표 활성화, 가게 번호표 비활성화, 공지사항 수정, 한사람당 대기시간 수정
     */
    @ApiOperation(value = "대기 인원 관리 + 가게 현황 관리 (번호표 관리)[가게 관리자]", notes = "대기 인원 명단과 가게 정보를 관리합니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @GetMapping("/tickets")
    public ResponseEntity manageMembersAndStore(Principal principal, Pageable pageable, PagedResourcesAssembler<WaitingMembersDto> assembler) {
        Store store = storeService.findValidStore(principal.getName());

        StoreManageDto dto = StoreManageDto.builder()
                .waitingMembers(assembler.toModel(storeService.findWaitingMembers(principal.getName(), pageable), e -> new WaitingMembersResource(e)))
                .storeTicketStatus(store.getStoreTicketStatus())
                .totalWaitingCount(store.getTotalWaitingCount())
                .totalWaitingTime(store.getTotalWaitingTime())
                .notice(store.getNotice())
                .avgWaitingTimeByOne(store.getAvgWaitingTimeByOne())
                //TODO 보류회원 결정나면 작업
                .build();
        //hateoas
        WaitingMembersAndStoreManageResource resource = new WaitingMembersAndStoreManageResource(dto);
        return ResponseEntity.ok(resource);
    }

    /**
     * [관리자] 보류된 회원 리스트 관리
     * 응답 ; 이름, 전화번호
     * link : 보류 ticket별 취소, 체크,
     */
    @ApiOperation(value = "보류 인원 관리 (번호표 관리)[가게 관리자]", notes = "보류 인원 명단을 관리합니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @GetMapping("/tickets/hold")
    public ResponseEntity manageHoldMembers(Principal principal, Pageable pageable, PagedResourcesAssembler<HoldingMembersDto> assembler) {

        //보류회원정보
        PagedModel<EntityModel<HoldingMembersDto>> holdingMembers =
                assembler.toModel(storeService.findHoldMembers(principal.getName(), pageable), e -> new HoldingMembersResource(e));

        return ResponseEntity.ok(holdingMembers);
    }
    

    /**
     * [관리자]체크하기
     */
    @ApiOperation(value = "현재 대기중인 번호표 체크하기[가게 관리자]", notes = "현재 대기 회원의 번호표를 체크합니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PostMapping("/tickets/{ticket_id}")
    public ResponseEntity checkTicket(@PathVariable("ticket_id")Long ticket_id, Principal principal) {
        storeService.checkTicket(principal.getName(), ticket_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("번호표 체크 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * [관리자]취소하기
     */
    @ApiOperation(value = "현재 대기중인 번호표 취소[가게 관리자]", notes = "현재 대기 회원의 번호표를 취소합니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @DeleteMapping("/tickets/{ticket_id}")
    public ResponseEntity cancelTicket(@PathVariable("ticket_id")Long ticket_id, Principal principal) {
        storeService.cancelTicketByAdmin(principal.getName(), ticket_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("번호표 취소 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * [관리자]보류하기
     */
    @ApiOperation(value = "현재 대기중인 번호표 보류[가게 관리자]", notes = "현재 대기 회원의 번호표를 보류합니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PutMapping("/tickets/{ticket_id}")
    public ResponseEntity holdTicket(@PathVariable("ticket_id")Long ticket_id, Principal principal) {
        storeService.holdTicket(principal.getName(), ticket_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("번호표 보류 성공")
                .build();
        return ResponseEntity.ok(response);
    }
    
    /**
     * [관리자] 보류회원 체크하기
     */
    @ApiOperation(value = "보류중인 번호표 체크[가게 관리자]", notes = "현재 보류중인 회원의 번호표를 체크합니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PostMapping("/tickets/{ticket_id}/hold")
    public ResponseEntity holdCheckTicket(@PathVariable("ticket_id")Long ticket_id, Principal principal) {
        storeService.holdCheckTicket(principal.getName(), ticket_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("보류 번호표 체크 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * [관리자] 보류회원 취소하기
     */
    @ApiOperation(value = "보류중인 번호표 취소[가게 관리자]", notes = "현재 보류중인 회원의 번호표를 취소합니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @DeleteMapping("/tickets/{ticket_id}/hold")
    public ResponseEntity holdCancelTicket(@PathVariable("ticket_id")Long ticket_id, Principal principal) {
        storeService.holdCancelTicket(principal.getName(), ticket_id);
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("보류 번호표 취소 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 가게 번호표 활성화
     */
    @ApiOperation(value = "가게 번호표 OPEN[가게 관리자]", notes = "가게 번호표 뽑기 기능을 활성화시킵니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PostMapping("/status")
    public ResponseEntity openTicket(Principal principal) {
        storeService.openTicket(principal.getName());
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("가게 번호표 활성화 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 가게 번호표 비활성화
     */
    @ApiOperation(value = "가게 번호표 CLOSE[가게 관리자]", notes = "가게 번호표 뽑기 기능을 비활성화시킵니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PutMapping("/status")
    public ResponseEntity closeTicket(Principal principal) {
        storeService.closeTicket(principal.getName());
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("가게 번호표 비활성화 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 오류 신청
     */
    @ApiOperation(value = "오류 신청[가게 관리자]", notes = "사이트 관리자한테 오류가 났다고 알림을 보냅니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PostMapping("/errors")
    public ResponseEntity sendErrorSystem(Principal principal) {
        storeService.sendErrorSystem(principal.getName());
        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("오류접수 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 공지사항 수정
     */
    @ApiOperation(value = "가게 공지사항 수정[가게 관리자]", notes = "가게의 공지사항을 수정할수 있습니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PutMapping("/notice")
    public ResponseEntity updateNotice(Principal principal, @RequestBody @Valid StoreNoticeForm form) {
        storeService.updateStoreNotice(principal.getName(), form.getNotice());

        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("공지사항 변경 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 한사람당 대기시간 수정
     */
    @ApiOperation(value = "가게 한 사람당 대기시간 수정[가게 관리자]", notes = "가게의 한 사람당 대기시간을 설정할 수 있습니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PutMapping("/time")
    public ResponseEntity updateAvgWaitingTime(Principal principal, @RequestBody @Valid AvgTimeForm form) {
        storeService.updateAvgTime(principal.getName(), form.getAvgWaitingTimeByOne());

        Response response = Response.builder()
                .result("success")
                .status(200)
                .message("대기시간 변경 성공")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 가게 수정
     */

//===========================================가게 찾기 메뉴========================================

    /**
     * 가게 보기
     */


    /**
     * 가게 상세보기
     */

    /**
     * 가게이름으로 검색
     */

}
