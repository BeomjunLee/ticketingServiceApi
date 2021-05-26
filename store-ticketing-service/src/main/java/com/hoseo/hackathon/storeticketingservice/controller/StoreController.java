package com.hoseo.hackathon.storeticketingservice.controller;
import com.hoseo.hackathon.storeticketingservice.domain.Store;
import com.hoseo.hackathon.storeticketingservice.domain.Ticket;
import com.hoseo.hackathon.storeticketingservice.domain.dto.HoldingMembersDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.WaitingMembersDto;
import com.hoseo.hackathon.storeticketingservice.domain.dto.StoreManageDto;
import com.hoseo.hackathon.storeticketingservice.domain.form.StoreInfoForm;
import com.hoseo.hackathon.storeticketingservice.domain.form.TicketForm;
import com.hoseo.hackathon.storeticketingservice.domain.resource.HoldingMembersResource;
import com.hoseo.hackathon.storeticketingservice.domain.resource.WaitingMembersAndStoreManageResource;
import com.hoseo.hackathon.storeticketingservice.domain.resource.WaitingMembersResource;
import com.hoseo.hackathon.storeticketingservice.domain.response.Response;
import com.hoseo.hackathon.storeticketingservice.domain.response.ResultStatus;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/stores", produces = MediaTypes.HAL_JSON_VALUE)
/**
 * 매장용
 */
public class StoreController {

    private final StoreService storeService;

//===========================================번호표 뽑기========================================

    /**
     * [회원] 번호표 뽑기
     * @param store_id 매장 고유 id 값
     * @param ticketForm 번호표 발급 form
     * @param principal jwt 요청 회원 정보
     * @return http response
     */
    @ApiOperation(value = "번호표 뽑기[회원]", notes = "매장의 번호표를 발급 받습니다")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{store_id}/tickets/new")
    public ResponseEntity createTicket(@PathVariable("store_id") Long store_id, @Valid @RequestBody TicketForm ticketForm, Principal principal) {

        storeService.createTicket(ticketForm, store_id, principal.getName());

        URI createdUri = linkTo(StoreController.class).slash(store_id).slash("tickets/new").toUri();

        return ResponseEntity.created(createdUri).body(Response.builder()
                                                        .result(ResultStatus.SUCCESS)
                                                        .status(200)
                                                        .message("번호표 발급 성공")
                                                        .build());
    }

    //===========================================가게 번호표 관리 메뉴========================================

    /**
     * 매장 번호표 리스트 관리 (보류 번호표는 x)
     * @param principal access_token 에서 추출한 회원 정보
     * @param pageable 페이징
     * @param assembler 페이징 관련 hateoas
     * @return http response
     */
    @ApiOperation(value = "대기 인원 관리 + 매장 현황 관리 (번호표 관리)[매장 관리자]", notes = "대기 인원 명단과 매장 정보를 관리합니다")
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
        return ResponseEntity.ok(new WaitingMembersAndStoreManageResource(dto));
    }

    /**
     * 보류된 번호표 리스트 관리
     * @param principal access_token 에서 추출한 회원 정보
     * @param pageable 페이징
     * @param assembler 페이징 관련 hateoas
     * @return http response
     */
    @ApiOperation(value = "보류 인원 관리 (번호표 관리)[매장 관리자]", notes = "보류 인원 명단을 관리합니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @GetMapping("/tickets/holding")
    public ResponseEntity manageHoldMembers(Principal principal, Pageable pageable, PagedResourcesAssembler<HoldingMembersDto> assembler) {

        //보류회원정보
        PagedModel<EntityModel<HoldingMembersDto>> holdingMembers =
                assembler.toModel(storeService.findHoldMembers(principal.getName(), pageable), e -> new HoldingMembersResource(e));

        return ResponseEntity.ok(holdingMembers);
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
        storeService.checkTicket(principal.getName(), ticket_id);

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
        storeService.cancelTicket(principal.getName(), ticket_id);

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
        storeService.holdTicket(principal.getName(), ticket_id);

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
        storeService.holdCheckTicket(principal.getName(), ticket_id);

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
        storeService.holdCancelTicket(principal.getName(), ticket_id);

        return ResponseEntity.ok(Response.builder()
                .result(ResultStatus.SUCCESS)
                .status(200)
                .message("보류 번호표 취소 성공")
                .build());
    }

    /**
     * 번호표 발급 가능 상태 -> OPEN
     * @param principal access_token 에서 추출한 회원 정보
     * @return http response
     */
    @ApiOperation(value = "매장 번호표 OPEN[매장 관리자]", notes = "매장 번호표 뽑기 기능을 활성화시킵니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PostMapping("/open-status")
    public ResponseEntity openTicket(Principal principal) {
        storeService.openTicket(principal.getName());

        return ResponseEntity.ok(Response.builder()
                .result(ResultStatus.SUCCESS)
                .status(200)
                .message("매장 번호표 활성화 성공")
                .build());
    }

    /**
     * 번호표 발급 가능 상태 -> CLOSE
     * @param principal access_token 에서 추출한 회원 정보
     * @return http response
     */
    @ApiOperation(value = "매장 번호표 CLOSE[매장 관리자]", notes = "매장 번호표 뽑기 기능을 비활성화시킵니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PostMapping("/close-status")
    public ResponseEntity closeTicket(Principal principal) {
        storeService.closeTicket(principal.getName());

        return ResponseEntity.ok(Response.builder()
                .result(ResultStatus.SUCCESS)
                .status(200)
                .message("매장 번호표 비활성화 성공")
                .build());
    }

    /**
     * 오류 신고
     * @param principal access_token 에서 추출한 회원 정보
     * @return http response
     */
    @ApiOperation(value = "오류 신청[매장 관리자]", notes = "사이트 관리자한테 오류가 났다고 알림을 보냅니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PostMapping("/apply-errors")
    public ResponseEntity sendErrorSystem(Principal principal) {
        storeService.sendErrorSystem(principal.getName());

        return ResponseEntity.ok(Response.builder()
                .result(ResultStatus.SUCCESS)
                .status(200)
                .message("오류접수 성공")
                .build());
    }

    /**
     * 매장 상태 정보 수정 (공지사항, 한사람당 예상 대기시간)
     * @param principal access_token 에서 추출한 회원 정보
     * @param form 매장 상태 정보 form
     * @return http response
     */
    @ApiOperation(value = "매장 상태 정보 수정[매장 관리자]", notes = "매장의 상태 정보를 수정할수 있습니다")
    @PreAuthorize("hasRole('ROLE_STORE_ADMIN')")
    @PostMapping("/edit-notice")
    public ResponseEntity updateInfo(Principal principal, @RequestBody @Valid StoreInfoForm form) {
        storeService.updateStoreInfo(principal.getName(), form);

        return ResponseEntity.ok(Response.builder()
                .result(ResultStatus.SUCCESS)
                .status(200)
                .message("매장 상태 정보 변경 성공")
                .build());
    }


}
